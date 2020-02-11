package com.mycloud.usermanage.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 自定义sessionId获取
 * shiro默认只会从cookie中获取sessionId,改为先从http头部的token获取sessionId,没有则按默认方式获取
 */
public class MySessionManager extends DefaultWebSessionManager {

    public MySessionManager() {
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //header中不要用"Authorization",可能会有冲突
        String token = WebUtils.toHttp(request).getHeader("token");
        
        //如果header中有token,则其值为sessionId(比如token:f118275a-3145-40a2-86ac-e792798e4a8b)
        if (StringUtils.isNotBlank(token)) {
            //需要设置以下请求属性,同super.getSessionId()里的处理
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "Stateless request");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return token;
        } else {
            //否则按默认规则从header中的Cookie取sessionId(比如Cookie:JSESSIONID=f118275a-3145-40a2-86ac-e792798e4a8b)
            return super.getSessionId(request, response);
        }
    }
}
