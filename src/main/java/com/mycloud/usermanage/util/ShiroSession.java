package com.mycloud.usermanage.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

public class ShiroSession {

    public static void setCurrentAdminId(Integer id) {
        getSession().setAttribute("adminId", id);
    }

    public static Integer getCurrentAdminId() {
        return (Integer)getSession().getAttribute("adminId");
    }

    public static void setCurrentUserId(Integer id) {
        getSession().setAttribute("userId", id);
    }

    public static Integer getCurrentUserId() {
        return (Integer)getSession().getAttribute("userId");
    }

    public static void setAttribute(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static Object getAttribute(String key) {
        return getSession().getAttribute(key);
    }

    private static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }
}
