package com.mycloud.usermanage.util;

import com.mycloud.usermanage.shiro.UserNamePswdTypeToken;
import com.mycloud.usermanage.shiro.UserType;
import com.mycloud.usermanage.pojo.BaseResponse;
import com.mycloud.usermanage.pojo.ExtraParamResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroLogin {

    private static Logger logger = LoggerFactory.getLogger(ShiroLogin.class);
    
    public static BaseResponse login(String name, String pswd, UserType type) {
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(pswd)) {
            UserNamePswdTypeToken token = new UserNamePswdTypeToken(name, pswd, type);
            Subject currentUser = SecurityUtils.getSubject();
            try {
                currentUser.login(token);
                Session sess = currentUser.getSession();
                
                if(type == UserType.ADMIN) {
                    //对于后台用户,shiro设置session在30分钟后失效
                    sess.setTimeout(1800000);
                } else {
                    //对于app用户,shiro设置session永不失效
                    sess.setTimeout(-1);
                }
                String sessionId = String.valueOf(sess.getId());
                return new ExtraParamResponse<>(sessionId);
            } catch (UnknownAccountException uae) {
                return BaseResponse.loginFailed("用户名或密码错误!");
            } catch (IncorrectCredentialsException ice) {
                return BaseResponse.loginFailed("用户名或密码错误!");
            } catch (LockedAccountException lae) {
                return BaseResponse.loginFailed("用户已经被锁定!");
            } catch (AuthenticationException ae) {
                logger.error("authentication error", ae);
                return BaseResponse.loginFailed("登录失败!");
            }
        } else {
            return BaseResponse.loginFailed("请填写用户名和密码!");
        }
    }
}
