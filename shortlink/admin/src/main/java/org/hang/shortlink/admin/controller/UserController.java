package org.hang.shortlink.admin.controller;


import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.hang.shortlink.admin.pojo.dto.req.UserLoginDto;
import org.hang.shortlink.admin.pojo.dto.req.UserReqDto;
import org.hang.shortlink.admin.pojo.dto.resp.UserActualRespDto;
import org.hang.shortlink.admin.pojo.dto.resp.UserInfoRespDto;
import org.hang.shortlink.admin.pojo.dto.resp.UserLoginRespDto;
import org.hang.shortlink.admin.pojo.po.User;
import org.hang.shortlink.admin.service.UserService;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.common.utils.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-17
 */
@RestController
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息查询相关接口")
public class UserController {

    private final UserService userService;

    /**
     *  根据用户名查找用户信息（脱敏信息）
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    @Operation(summary = "查询用户信息（脱敏信息）", description = "根据用户名查询用户基本信息")
    public Result<UserInfoRespDto> getUserInfo(@PathVariable("username") String username){
        User user = userService.getUserByName(username);
        return Result.success(BeanUtil.toBean(user,UserInfoRespDto.class));
    }

    /**
     *  根据用户名查找用户信息
     */
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    @Operation(summary = "查询用户实名信息", description = "根据用户名查询用户实名认证信息")
    public Result<UserActualRespDto> getActualUserInfo(@PathVariable("username") String username){
        User user = userService.getUserByName(username);
        return Result.success(BeanUtil.toBean(user,UserActualRespDto.class));
    }

    /**
     * 查询用户名是否可用
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    @Operation(summary = "检查用户名是否可用", description = "检查用户名是否已被注册")
    public Result<Boolean> isAvailableOfUsername(@Param("username") String username){
        Boolean available = userService.isAvailableOfUsername(username);
        return Result.success(available);
    }

    /**
     * 用户注册
     */
    @PostMapping("/api/short-link/admin/v1/user/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result saveUser(@RequestBody @Validated UserReqDto userReqDto) {
        userService.saveUser(userReqDto);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @PutMapping("/api/short-link/admin/v1/user")
    @Operation(summary = "修改用户信息", description = "更新用户基本信息")
    public Result updateUser(@RequestBody @Validated UserReqDto userReqDto) {
        userService.updateUser(userReqDto);
        return Result.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回登录凭证")
    public Result<UserLoginRespDto> login(@RequestBody @Validated UserLoginDto loginDto) {
        UserLoginRespDto respDto = userService.login(loginDto);
        return Result.success(respDto);
    }

    /**
     * 检查用户是否登录
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    @Operation(summary = "检查登录状态", description = "检查当前用户是否已登录")
    public Result<Boolean> isLogin(HttpServletRequest request) {
        String username = UserContext.getUserInfo();
        String token = request.getHeader("token");
        Boolean isLogin = userService.checkLogin(username, token);
        return Result.success(isLogin);
    }

    /**
     * 退出登录
     */
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    @Operation(summary = "退出登录", description = "用户退出登录接口")
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        userService.logout(token);
        return Result.success();
    }
}
