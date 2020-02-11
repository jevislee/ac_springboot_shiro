package com.mycloud.usermanage.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mycloud.usermanage.entity.*;
import com.mycloud.usermanage.mapper.*;
import com.mycloud.usermanage.pojo.BaseResponse;
import com.mycloud.usermanage.pojo.ExtraParamResponse;
import com.mycloud.usermanage.util.ShiroSession;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@io.swagger.annotations.Api(tags="权限接口")
@RestController
public class PrivilegeController {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private ExcludedApiMapper excludedApiMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleApiMapper roleApiMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;


    @ApiOperation(value = "自动发现系统里的接口", produces="application/json")
    @GetMapping("/autoRegisterApi")
    public BaseResponse autoRegisterApi() {
        Set<String> urlList = new TreeSet<>();
        List<String> savedUrlList = apiMapper.queryApiUri();
        List<String> excludedUrlList = excludedApiMapper.queryExcludedApiUri();

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                urlList.add(url);
            }
        }

        urlList.removeAll(savedUrlList);
        urlList.removeAll(excludedUrlList);

        List<Api> newApiList = new ArrayList<>();
        for (String url : urlList) {
            Api api = new Api();
            api.setUri(url);
            apiMapper.insertSelective(api);
            newApiList.add(api);
        }
        return new ExtraParamResponse<>(newApiList);
    }


    @ApiOperation(value = "分页查询接口", produces="application/json")
    @GetMapping("/queryAllApi")
    public BaseResponse queryAllApi(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                    @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String title, @RequestParam(required = false) String uri) {
        PageHelper.startPage(currentPage, pageSize);

        if(StringUtils.isBlank(title)) {
            title = null;
        }
        if(StringUtils.isBlank(uri)) {
            uri = null;
        }

        return new ExtraParamResponse<>(new PageInfo(apiMapper.queryAllApi(title, uri)));
    }

    @ApiOperation(value = "修改接口", produces="application/json")
    @PostMapping("/modifyApi")
    public BaseResponse modifyApi(@RequestBody Api param) {
        return new ExtraParamResponse<>(apiMapper.updateByPrimaryKeySelective(param));
    }

    @ApiOperation(value = "删除接口", produces="application/json")
    @PostMapping("/deleteApi")
    public BaseResponse deleteApi(@RequestParam Integer id) {
        if(roleApiMapper.queryApiUseCount(id) == 0) {
            return new ExtraParamResponse<>(apiMapper.deleteByPrimaryKey(id));
        } else {
            return BaseResponse.invalidParam("Api正在被使用");
        }
    }

    @ApiOperation(value = "排除接口", produces="application/json")
    @PostMapping("/moveApiToExcluded")
    public BaseResponse moveApiToExcluded(@RequestParam Integer id) {
        if(roleApiMapper.queryApiUseCount(id) == 0) {
            Api api = apiMapper.selectByPrimaryKey(id);

            apiMapper.deleteByPrimaryKey(id);

            ExcludedApi excludedApi = new ExcludedApi();
            excludedApi.setUri(api.getUri());
            excludedApiMapper.insertSelective(excludedApi);

            return BaseResponse.SUCCESS;
        } else {
            return BaseResponse.invalidParam("Api正在被使用");
        }
    }


    @ApiOperation(value = "查询排除的接口", produces="application/json")
    @GetMapping("/queryExcludedApi")
    public BaseResponse queryExcludedApi(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                         @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        return new ExtraParamResponse<>(new PageInfo(excludedApiMapper.queryExcludedApi()));
    }

    @ApiOperation(value = "删除排除的接口", produces="application/json")
    @PostMapping("/deleteExcludedApi")
    public BaseResponse deleteExcludedApi(@RequestParam Integer id) {
        return new ExtraParamResponse<>(excludedApiMapper.deleteByPrimaryKey(id));
    }

    @ApiOperation(value = "分页查询菜单", produces="application/json")
    @GetMapping("/queryAllMenu")
    public BaseResponse queryAllMenu(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                     @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        return new ExtraParamResponse<>(new PageInfo(menuMapper.queryAllMenu()));
    }

    @ApiOperation(value = "查询一级菜单", produces="application/json")
    @GetMapping("/queryTopLevelMenu")
    public BaseResponse queryTopLevelMenu() {
        List topLevelMenuList = menuMapper.queryTopLevelMenu();
        Menu m = new Menu();
        m.setId(0);
        m.setTitle("无");
        topLevelMenuList.add(0, m);
        return new ExtraParamResponse<>(topLevelMenuList);
    }

    @ApiOperation(value = "查询两级菜单", produces="application/json")
    @GetMapping("/queryMenuTree")
    public BaseResponse queryMenuTree() {
        List<Menu> menuList = null;

        Integer adminId = ShiroSession.getCurrentAdminId();
        if(adminId == 1) {
            menuList = menuMapper.queryAllMenu();
        } else {
            menuList = menuMapper.queryMenuByAdminId(adminId);
        }

        for(Menu menu : menuList) {
            menu.meta = new LinkedHashMap();
            if(StringUtils.isNotBlank(menu.getTitle())) {
                menu.meta.put("title", menu.getTitle());
            }

            if(StringUtils.isNotBlank(menu.getIcon())) {
                menu.meta.put("icon", menu.getIcon());
            }

            menu.setTitle(null);
            menu.setIcon(null);
        }

        List<Menu> menuTree = new ArrayList();
        List<Menu> childMenu = new ArrayList();
        for(Menu menu : menuList) {
            if(menu.getParentId() == 0) {
                menuTree.add(menu);
            } else {
                childMenu.add(menu);
            }
        }

        for(Menu parent: menuTree) {
            for (Menu menu : childMenu) {
                if (menu.getParentId() == parent.getId()) {
                    menu.setId(null);
                    menu.setParentId(null);
                    menu.setRedirect(null);
                    menu.setOrderNo(null);
                    menu.parentTitle = null;
                    menu.children = null;

                    if(parent.children == null) {
                        parent.children = new ArrayList();
                    }
                    parent.children.add(menu);
                }
            }

            parent.setId(null);
            parent.setParentId(null);
            parent.setOrderNo(null);
            parent.parentTitle = null;
        }

        return new ExtraParamResponse<>(menuTree);
    }

    @ApiOperation(value = "新增菜单", produces="application/json")
    @PostMapping("/addMenu")
    public BaseResponse addMenu(@RequestBody Menu param) {
        if(param.getParentId() == null || param.getParentId() == 0) {
            return new ExtraParamResponse<>(menuMapper.insertSelective(param));
        } else {
            if(menuMapper.isTopLevelMenu(param.getParentId()) > 0) {
                return new ExtraParamResponse<>(menuMapper.insertSelective(param));
            } else {
                return BaseResponse.invalidParam("只支持两级菜单");
            }
        }
    }

    @ApiOperation(value = "修改菜单", produces="application/json")
    @PostMapping("/modifyMenu")
    public BaseResponse modifyMenu(@RequestBody Menu param) {
        if(param.getParentId() == null || param.getParentId() == 0) {
            return new ExtraParamResponse<>(menuMapper.updateByPrimaryKeySelective(param));
        } else if(param.getParentId().equals(param.getId())) {
            return BaseResponse.invalidParam("不能添加自己为子菜单");
        } else {
            if(menuMapper.isTopLevelMenu(param.getParentId()) > 0) {
                if(menuMapper.hasChildMenu(param.getId()) == 0) {
                    return new ExtraParamResponse<>(menuMapper.updateByPrimaryKeySelective(param));
                } else {
                    return BaseResponse.invalidParam("该菜单已有子菜单,不能降为子菜单");
                }
            } else {
                return BaseResponse.invalidParam("只支持两级菜单");
            }
        }
    }

    @ApiOperation(value = "删除菜单", produces="application/json")
    @PostMapping("/deleteMenu")
    public BaseResponse deleteMenu(@RequestParam Integer id) {
        if(roleMenuMapper.queryMenuUseCount(id) == 0) {
            return new ExtraParamResponse<>(menuMapper.deleteByPrimaryKey(id));
        } else {
            return BaseResponse.invalidParam("菜单正在被使用");
        }
    }


    @ApiOperation(value = "分页查询所有角色", produces="application/json")
    @GetMapping("/queryAllRoles")
    public BaseResponse queryAllRoles(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                      @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        return new ExtraParamResponse<>(new PageInfo(roleMapper.queryAllRoles()));
    }

    @ApiOperation(value = "新增角色", produces="application/json")
    @PostMapping("/addRole")
    public BaseResponse addRole(@RequestBody Role param) {
        return new ExtraParamResponse<>(roleMapper.insertSelective(param));
    }

    @ApiOperation(value = "修改角色", produces="application/json")
    @PostMapping("/modifyRole")
    public BaseResponse modifyRole(@RequestBody Role param) {
        return new ExtraParamResponse<>(roleMapper.updateByPrimaryKeySelective(param));
    }

    @ApiOperation(value = "删除角色", produces="application/json")
    @PostMapping("/deleteRole")
    public BaseResponse deleteRole(@RequestParam Integer id) {
        if(roleMapper.queryRoleUseCount(id) == 0) {
            return new ExtraParamResponse<>(roleMapper.deleteByPrimaryKey(id));
        } else {
            return BaseResponse.invalidParam("角色正在被使用");
        }
    }


    @ApiOperation(value = "为角色添加权限", produces="application/json")
    @PostMapping("/addRolePrivilege")
    public BaseResponse addRolePrivilege(@RequestParam Integer roleId, @RequestParam Integer privilegeId, @RequestParam Integer type) {
        if (type == 0) {//API权限
            RoleApi roleApi = new RoleApi();
            roleApi.setRoleId(roleId);
            roleApi.setApiId(privilegeId);
            return new ExtraParamResponse<>(roleApiMapper.insertSelective(roleApi));
        } else {//菜单权限
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(privilegeId);
            return new ExtraParamResponse<>(roleMenuMapper.insertSelective(roleMenu));
        }
    }

    @ApiOperation(value = "删除角色的权限", produces="application/json")
    @PostMapping("/deleteRolePrivilege")
    public BaseResponse deleteRolePrivilege(@RequestParam Integer roleId, @RequestParam Integer privilegeId, @RequestParam Integer type) {
        if (type == 0) {//API权限
            return new ExtraParamResponse<>(roleApiMapper.deleteByRoleIdAndApiId(roleId, privilegeId));
        } else {//菜单权限
            return new ExtraParamResponse<>(roleMenuMapper.deleteByRoleIdAndMenuId(roleId, privilegeId));
        }
    }

    @ApiOperation(value = "分页查询角色的API权限", produces="application/json")
    @GetMapping("/queryApiForRole")
    public BaseResponse queryApiForRole(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                        @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                        @RequestParam Integer roleId, @RequestParam Integer type,
                                        @RequestParam String title, @RequestParam String uri) {
        PageHelper.startPage(currentPage, pageSize);

        if(StringUtils.isBlank(title)) {
            title = null;
        }
        if(StringUtils.isBlank(uri)) {
            uri = null;
        }

        return new ExtraParamResponse<>(new PageInfo(apiMapper.queryApiForRole(roleId, type, title, uri)));
    }

    @ApiOperation(value = "分页查询角色的菜单权限", produces="application/json")
    @GetMapping("/queryMenuForRole")
    public BaseResponse queryMenuForRole(@ApiParam("当前页") @RequestParam(required = false,defaultValue = "1") Integer currentPage,
                                        @ApiParam("每页记录数") @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                        @RequestParam Integer roleId, @RequestParam Integer type,
                                        @RequestParam String title, @RequestParam String path) {
        PageHelper.startPage(currentPage, pageSize);

        if(StringUtils.isBlank(title)) {
            title = null;
        }
        if(StringUtils.isBlank(path)) {
            path = null;
        }

        return new ExtraParamResponse<>(new PageInfo(menuMapper.queryMenuForRole(roleId, type, title, path)));
    }
}  