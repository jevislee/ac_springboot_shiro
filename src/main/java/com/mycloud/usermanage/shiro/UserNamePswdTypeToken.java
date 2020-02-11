package com.mycloud.usermanage.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 用于区分不同的用户类型(比如后台管理用户,app用户,微信用户等)
 */
public class UserNamePswdTypeToken extends UsernamePasswordToken {

    private UserType userType;

    public UserNamePswdTypeToken(String loginName, String password, UserType userType) {
        super(loginName, password);
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
