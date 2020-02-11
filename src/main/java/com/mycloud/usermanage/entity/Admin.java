package com.mycloud.usermanage.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Admin implements Serializable {
    private Integer id;

    private String name;

    private String pswd;

    private Integer status;

    private Date lastLoginTime;

    private Date createTime;

    private Date updateTime;

    private Date deleteTime;

    private List<String> roleStrlist;
    private List<String> apiPermStrlist;

    public String oldPswd;
    
    public String token;
            
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public List<String> getRoleStrlist() {
        return roleStrlist;
    }

    public void setRoleStrlist(List<String> roleStrlist) {
        this.roleStrlist = roleStrlist;
    }

    public List<String> getApiPermStrlist() {
        return apiPermStrlist;
    }

    public void setApiPermStrlist(List<String> apiPermStrlist) {
        this.apiPermStrlist = apiPermStrlist;
    }
}