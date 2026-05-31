package org.hang.shortlink.admin.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.admin.pojo.dto.req.GroupReqDto;
import org.hang.shortlink.admin.pojo.po.Group;
import org.hang.shortlink.admin.service.GroupService;
import org.hang.shortlink.common.pojo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-18
 */
@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Tag(name = "短链接分组管理", description = "短链接分组相关操作接口")
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     */
    @PostMapping("/api/short-link/admin/v1/group")
    @Operation(summary = "新增短链接分组", description = "创建一个新的短链接分组")
    public Result<Boolean> addGroup(@RequestBody GroupReqDto reqDto){
        Boolean flag = groupService.addGroup(reqDto);
        return Result.success(flag);
    }

    /**
     * 查询分组集合
     */
    @GetMapping("/api/short-link/admin/v1/group")
    @Operation(summary = "查询分组集合", description = "获取当前用户的所有短链接分组")
    public Result<List<Group>> queryGroup(){
        List<Group> list = groupService.queryGroup();
        return Result.success(list);
    }

    /**
     * 修改短链接分组
     */
    @PutMapping("/api/short-link/admin/v1/group")
    @Operation(summary = "修改短链接分组", description = "更新指定短链接分组的信息")
    public Result<Void> updateGroup(@RequestBody GroupReqDto reqDto){
        groupService.updateGroup(reqDto);
        return Result.success();
    }


    /**
     * 删除短链接分组
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    @Operation(summary = "删除短链接分组", description = "删除指定的短链接分组")
    public Result<Void> removeGroup(String gid){
        groupService.removeGroup(gid);
        return Result.success();
    }

    /**
     * 排序短链接分组
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    @Operation(summary = "排序短链接分组", description = "调整短链接分组的显示顺序")
    public Result<Void> sortGroup(@RequestBody List<GroupReqDto> reqDto){
        groupService.sortGroup(reqDto);
        return Result.success();
    }

//    /**
//     * 创建默认分组
//     */
//    @PostMapping("/api/short-link/admin/v1/group/default")
//    @Operation(summary = "创建默认分组", description = "为新用户创建默认短链接分组")
//    public Result<Void> createDefaultGroup(
//            @Parameter(name = "username", description = "用户名", required = true)
//            @RequestParam("username") String username){
//        groupService.addDefaultGroup(username);
//        return Result.success();
//    }
}
