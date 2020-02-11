package com.mycloud.usermanage.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mycloud.usermanage.entity.Admin;
import com.mycloud.usermanage.mapper.AdminMapper;
import com.mycloud.usermanage.mapper.RoleMapper;
import com.mycloud.usermanage.pojo.BaseResponse;
import com.mycloud.usermanage.pojo.ExtraParamResponse;
import com.mycloud.usermanage.shiro.UserType;
import com.mycloud.usermanage.util.ShiroLogin;
import com.mycloud.usermanage.util.ShiroSession;
import com.mycloud.usermanage.mapper.ApiMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private RoleMapper roleMapper;

    public BaseResponse login(Admin param) {
        BaseResponse response = ShiroLogin.login(param.getName(), param.getPswd(), UserType.ADMIN);
        if(response.isSuccess()) {
            Admin admin = adminMapper.queryAdmin(param.getName());
            admin.token = (String)((ExtraParamResponse)response).extra;
            ShiroSession.setCurrentAdminId(admin.getId());

            Admin adminUpdate = new Admin();
            adminUpdate.setId(admin.getId());
            adminUpdate.setLastLoginTime(new Date());
            adminMapper.updateByPrimaryKeySelective(adminUpdate);

            admin.setPswd(null);
            return new ExtraParamResponse<>(admin);
        } else {
            return response;
        }
    }
    
    public BaseResponse queryList(Admin param, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);

        List<Admin> list = adminMapper.queryList(param);
        return new ExtraParamResponse<>(new PageInfo(list));
    }

    public Admin queryById(Integer id) {
        Admin admin = adminMapper.selectByPrimaryKey(id);
        if(admin != null) {
            admin.setPswd(null);
        }
        return admin;
    }
    
    public BaseResponse add(Admin param) {
        return new ExtraParamResponse<>(adminMapper.insertSelective(param));
    }

    public BaseResponse update(Admin param) {
        if(param.oldPswd != null) {
            Admin admin = adminMapper.selectByPrimaryKey(param.getId());
            if(!admin.getPswd().equals(param.oldPswd)) {
                return BaseResponse.invalidParam("原密码错误!");
            } else if(StringUtils.isBlank(param.getPswd())) {
                return BaseResponse.invalidParam("密码不能设置为空!");
            } else if(param.getPswd().length() < 6) {
                return BaseResponse.invalidParam("密码长度不能小于6位!");
            }
        }
        return new ExtraParamResponse<>(adminMapper.updateByPrimaryKeySelective(param));
    }

    public BaseResponse delete(Integer id) {
        Admin param = new Admin();
        param.setId(id);
        param.setDeleteTime(new Date());
        return new ExtraParamResponse<>(adminMapper.updateByPrimaryKeySelective(param));
    }

    public BaseResponse queryApiPermNames() {
        Integer adminId = ShiroSession.getCurrentAdminId();
        if(adminId == 1) {
            return new ExtraParamResponse<>(apiMapper.queryApiUri());
        } else {
            return new ExtraParamResponse<>(apiMapper.queryApiPermNamesByAdminId(adminId));
        }
    }

    public BaseResponse queryRoleNames() {
        Integer adminId = ShiroSession.getCurrentAdminId();
        if(adminId == 1) {
            return new ExtraParamResponse<>(new String[] {"超级管理角色"});
        } else {
            return new ExtraParamResponse<>(roleMapper.queryRoleNamesByUserId(adminId));
        }
    }
}
