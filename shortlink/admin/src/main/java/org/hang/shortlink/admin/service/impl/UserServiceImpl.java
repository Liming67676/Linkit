package org.hang.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;

import org.hang.shortlink.admin.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hang.shortlink.admin.pojo.dto.req.UserLoginDto;
import org.hang.shortlink.admin.pojo.dto.req.UserReqDto;
import org.hang.shortlink.admin.pojo.dto.resp.UserLoginRespDto;
import org.hang.shortlink.admin.pojo.po.User;
import org.hang.shortlink.admin.service.GroupService;
import org.hang.shortlink.admin.service.UserService;
import org.hang.shortlink.common.constant.LockConstant;
import org.hang.shortlink.common.constant.RegConstant;
import org.hang.shortlink.admin.utils.PasswordEncoderUtil;
import org.hang.shortlink.common.constant.TokenConstant;
import org.hang.shortlink.common.exception.ServiceException;
import org.hang.shortlink.common.utils.JwtUtils;
import org.hang.shortlink.common.utils.UserContext;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-17
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtils jwtUtils;
    private final RBloomFilter<String> bloomFilter; // 布隆过滤器
    private final RedissonClient redissonClient;

    private final GroupService groupService;



    @Override
    public User getUserByName(String username) {
        return lambdaQuery().like(User::getUsername, username)
                .one();
    }

    @Override
    public Boolean isAvailableOfUsername(String username) {
//        User user = lambdaQuery().eq(User::getUsername, username).one();
        // 当用户恶意攻击时，简单采用redis会缓存穿透，这采用bloomFilter解决

        // bloom为假阳性，不存在直接相信，存在还要查数据库
        boolean existsInBloom = bloomFilter.contains(username);
        // 不存在，用户名可以，放回true
        if(!existsInBloom){
            return true;
        }
        // 存在
        Long count = lambdaQuery().eq(User::getUsername, username).count();
        return count == 0;
    }

    // 防止统一用户名统一时间批量注册
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUser(UserReqDto userReqDto) {
        checkUserReqDto(userReqDto);
        // 密码加密
        String encode = PasswordEncoderUtil.encode(userReqDto.getPassword());
        User newUser = BeanUtil.toBean(userReqDto, User.class);
        newUser.setPassword(encode);
        RLock lock = redissonClient.getLock(LockConstant.LOCK_USER_REGISTER + userReqDto.getUsername());
        if (lock.tryLock()) {
            try {
                // 加锁后，必须先查询数据库，校验用户名是否已存在
                User existUser = lambdaQuery().eq(User::getUsername, userReqDto.getUsername()).one();
                if (Objects.nonNull(existUser)) {
                    throw new ServiceException("用户名已存在");
                }
                // 不存在再执行保存
                boolean saved = save(newUser);
                if(saved){
                    bloomFilter.add(userReqDto.getUsername());
                    groupService.addDefaultGroup(userReqDto.getUsername());
                }
            } finally {
                lock.unlock();
            }
        } else {
            throw new ServiceException("已经存在用户名");
        }

    }
    @Override
    public void updateUser(UserReqDto userReqDto) {
        if(userReqDto == null){
            throw new ServiceException("用户信息错误");
        }
        // 用户信息
        String username = UserContext.getUserInfo();
        if (username == null) {
            throw new ServiceException("用户不存在");
        }
        // 手机号检查
        if (!Pattern.matches(RegConstant.phoneReg, userReqDto.getPhone())) {
            throw new ServiceException("手机号格式错误");
        }
        // 密码检查
        if(StrUtil.isNotEmpty(userReqDto.getPassword()) && !Pattern.matches(RegConstant.passwordReg, userReqDto.getPassword())){
            throw new ServiceException("密码格式错误");
        }
        // 邮箱检查
        if(StrUtil.isNotEmpty(userReqDto.getMail()) && !Pattern.matches(RegConstant.mailReg, userReqDto.getMail())){
            throw new ServiceException("邮箱错误");
        }
        boolean updated = lambdaUpdate()
                .set(User::getPhone, userReqDto.getPhone())
                .set(StrUtil.isNotEmpty(userReqDto.getPassword()),
                        User::getPassword, PasswordEncoderUtil.encode(userReqDto.getPassword()))
                .set(StrUtil.isNotEmpty(userReqDto.getMail()),
                        User::getMail, userReqDto.getMail())
                .set(StrUtil.isNotEmpty(userReqDto.getRealName()),User::getRealName,userReqDto.getRealName())
                .eq(User::getUsername,username)
                .update();
        if(!updated){
            throw new ServiceException("用户信息更新错误");
        }
    }

    private void checkUserReqDto(UserReqDto userReqDto) {
        if(userReqDto == null){
            throw new ServiceException("用户信息错误");
        }
        // 用户名唯一检查
        if(!isAvailableOfUsername(userReqDto.getUsername())){
            throw new ServiceException("用户名已存在");
        }
        // 手机号检查
        if(userReqDto.getPhone().isBlank() || !Pattern.matches(RegConstant.phoneReg, userReqDto.getPhone())){
            throw new ServiceException("手机号格式错误");
        }
        User user = lambdaQuery().eq(User::getPhone, userReqDto.getPhone()).one();
        if(user != null){
            throw new ServiceException("手机号已注册");
        }
        // 密码检查
        if(userReqDto.getPassword().isBlank() || !Pattern.matches(RegConstant.passwordReg, userReqDto.getPassword())){
            throw new ServiceException("密码格式错误");
        }
        // 邮箱检查
        if(userReqDto.getMail().isBlank() || !Pattern.matches(RegConstant.mailReg, userReqDto.getMail())){
            throw new ServiceException("邮箱错误");
        }
    }


    @Override
    public UserLoginRespDto login(UserLoginDto loginDto) {
        // 检查
        if(loginDto == null || loginDto.getUsername().isBlank() || loginDto.getPassword().isBlank()){
            throw new ServiceException("用户名或密码不能为空");
        }
        User user = lambdaQuery().eq(User::getUsername, loginDto.getUsername())
                .one();
        if(user == null){
            throw new ServiceException("用户不存在");
        }
        if(!PasswordEncoderUtil.matches(loginDto.getPassword(),user.getPassword())){
            throw new ServiceException("密码错误");
        }
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username",user.getUsername());
        String token = jwtUtils.generateToken(claims);

        // 将token存入Redis（支持多设备登录：每台设备独立token）
        stringRedisTemplate.opsForValue().set(
                String.format(TokenConstant.LOGIN_TOKEN_PREFIX, token),
                user.getUsername(),
                jwtUtils.getTtl(),
                TimeUnit.MILLISECONDS
        );

        UserLoginRespDto respDto = new UserLoginRespDto().setId(user.getId()).setToken(token);
        return respDto;
    }

    @Override
    public void logout(String token) {
        if (StrUtil.isBlank(token)) {
            return;
        }
        stringRedisTemplate.delete(String.format(TokenConstant.LOGIN_TOKEN_PREFIX, token));
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        if (StrUtil.isBlank(username) || StrUtil.isBlank(token)) {
            return false;
        }
        String redisUsername = stringRedisTemplate.opsForValue().get(String.format(TokenConstant.LOGIN_TOKEN_PREFIX, token));
        return username.equals(redisUsername);
    }

}
