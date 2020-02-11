package com.mycloud.usermanage.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mycloud.usermanage.entity.Admin;
import com.mycloud.usermanage.entity.AdminRole;
import com.mycloud.usermanage.mapper.AdminMapper;
import com.mycloud.usermanage.entity.*;
import com.mycloud.usermanage.impl.AdminServiceImpl;
import com.mycloud.usermanage.mapper.AdminRoleMapper;
import com.mycloud.usermanage.mapper.RoleMapper;
import com.mycloud.usermanage.pojo.BaseResponse;
import com.mycloud.usermanage.pojo.ExtraParamResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags="后台管理用户接口")
@RestController
public class AdminController {

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminRoleMapper adminRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @ApiOperation(value = "后台管理用户登录", produces="application/json")
    @PostMapping("/admin/login")
    public BaseResponse login(@RequestBody Admin param) {
        return adminService.login(param);
    }

    @ApiOperation(value = "返回当前后台管理用户的所有权限给前端控制按钮显示", produces="application/json")
    @GetMapping("/admin/queryApiPermNames")
    public BaseResponse queryApiPermNames() {
        return adminService.queryApiPermNames();
    }

    @ApiOperation(value = "返回当前后台管理用户的所有角色", produces="application/json")
    @GetMapping("/admin/queryRoleNames")
    public BaseResponse queryRoleNames() {
        return adminService.queryRoleNames();
    }


    @ApiOperation(value = "分页查询管理用户", produces="application/json")
    @GetMapping("/queryAllAdmins")
    public BaseResponse queryAllAdmins(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                           @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        return new ExtraParamResponse<>(new PageInfo(adminMapper.queryList(null)));
    }

    @ApiOperation(value = "新增管理用户", produces="application/json")
    @PostMapping("/addAdmin")
    public BaseResponse addAdmin(@RequestBody Admin param) {
        if(adminMapper.queryAdmin(param.getName()) == null) {
            return new ExtraParamResponse<>(adminMapper.insertSelective(param));
        } else {
            return BaseResponse.invalidParam("用户名已存在!");
        }
    }

    @ApiOperation(value = "修改管理用户", produces="application/json")
    @PostMapping("/modifyAdmin")
    public BaseResponse modifyAdmin(@RequestBody Admin param) {
        return new ExtraParamResponse<>(adminMapper.updateByPrimaryKeySelective(param));
    }

    @ApiOperation(value = "删除管理用户", produces="application/json")
    @PostMapping("/deleteAdmin")
    public BaseResponse deleteAdmin(@RequestParam Integer id) {
        adminRoleMapper.deleteByAdminId(id);
        return new ExtraParamResponse<>(adminMapper.deleteByPrimaryKey(id));
    }


    @ApiOperation(value = "为管理用户新增角色", produces="application/json")
    @PostMapping("/addAdminRole")
    public BaseResponse addAdminRole(@RequestParam Integer adminId, @RequestParam Integer roleId) {
        AdminRole adminRole = new AdminRole();
        adminRole.setAdminId(adminId);
        adminRole.setRoleId(roleId);
        return new ExtraParamResponse<>(adminRoleMapper.insertSelective(adminRole));
    }

    @ApiOperation(value = "删除管理用户的角色", produces="application/json")
    @PostMapping("/deleteAdminRole")
    public BaseResponse deleteAdminRole(@RequestParam Integer adminId, @RequestParam Integer roleId) {
            return new ExtraParamResponse<>(adminRoleMapper.deleteByAdminIdAndRoleId(adminId, roleId));
    }

    @ApiOperation(value = "分页查询管理用户的角色", produces="application/json")
    @GetMapping("/queryRoleForAdmin")
    public BaseResponse queryRoleForAdmin(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                        @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                        @RequestParam Integer adminId, @RequestParam Integer type) {
        PageHelper.startPage(currentPage, pageSize);
        return new ExtraParamResponse<>(new PageInfo(roleMapper.queryRoleForAdmin(adminId, type)));
    }
}
