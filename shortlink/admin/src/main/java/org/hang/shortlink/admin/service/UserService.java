package org.hang.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.hang.shortlink.admin.pojo.dto.req.UserLoginDto;
import org.hang.shortlink.admin.pojo.dto.req.UserReqDto;
import org.hang.shortlink.admin.pojo.dto.resp.UserLoginRespDto;
import org.hang.shortlink.admin.pojo.po.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-17
 */
public interface UserService extends IService<User> {

    /**
     *根据用户名查找用户信息（脱敏信息）
     */
    User getUserByName(String username);

    /**
     * 查询用户名是否存在
     * 存在（false）,不存在（可加入true）
     */
    Boolean isAvailableOfUsername(String username);

    /**
     * 用户注册
     */
    void saveUser(UserReqDto userReqDto);

    /**
     * 用户修改
     */
    void updateUser(UserReqDto userReqDto);

    /**
     * 用户登录
     */
    UserLoginRespDto login(UserLoginDto loginDto);

    /**
     * 退出登录
     */
    void logout(String token);

    /**
     * 检查用户是否已登录（Redis中token是否存在）
     */
    Boolean checkLogin(String username, String token);

}
