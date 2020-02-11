package com.mycloud.usermanage.shiro;

import com.mycloud.usermanage.entity.Admin;
import com.mycloud.usermanage.entity.User;
import com.mycloud.usermanage.mapper.AdminMapper;
import com.mycloud.usermanage.mapper.UserMapper;
import com.mycloud.usermanage.mapper.ApiMapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 经测试得出:
 * 该类的doGetAuthenticationInfo接口只会在usermanage中调用shiro的Subject.login()时被调用
 * 该类的doGetAuthorizationInfo接口只会在zuul中当shiro filter拦截用户请求时被调用
 */
public class MyShiroRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private UserMapper userMapper;
    
    private List<String> appApiPermNames;
    
    @PostConstruct
    public void initAppApiPermNames() {
        appApiPermNames = apiMapper.queryAppApiPermNames();
    }
    
    /**
     * 返回用户名对应的密码给shiro进行登录认证
     * 这个方法里可以抛出各种登录异常给controller层
     *
     * 每次登录时被shiro调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        UserNamePswdTypeToken token = (UserNamePswdTypeToken) authenticationToken;
        String username = token.getUsername();
        UserType userType = token.getUserType();

        if(userType == UserType.ADMIN) {
            Admin admin = adminMapper.queryAdmin(username);//!!!这里验证用户是否存在
            if (admin != null) {
                if(admin.getStatus() == 2) {
                    throw new LockedAccountException();
                }

                if(username.equals("admin")) {
                    List<String> apiPermStrlist = apiMapper.queryApiUri();
                    admin.setApiPermStrlist(apiPermStrlist);
                } else {
                    //用户的权限查询放在这里做是为了减少数据库查询(坏处是权限不会实时更新,用户要退出重新登录才会更新权限)
                    List<String> apiPermStrlist = apiMapper.queryApiPermNamesByAdminId(admin.getId());//用户的API权限集合
                    admin.setApiPermStrlist(apiPermStrlist);
                }

                return new SimpleAuthenticationInfo(admin, admin.getPswd(), getName());
            }
        } else {
            User user = userMapper.queryUser(username, userType.ordinal(), 1);
            if (user != null) {
                if(user.getStatus() == 2) {
                    throw new LockedAccountException();
                }

                String pswd = user.getPswd();
                user.setPswd(null);
                return new SimpleAuthenticationInfo(user, pswd, getName());
            }
        }
        return null;
    }

    /**
     * 返回用户的角色权限信息给shiro进行访问认证
     *
     * 登录后只被shiro调用一次且存储到缓存,之后shiro从缓存里获取
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object principal = principalCollection.getPrimaryPrincipal();
        if(principal instanceof Admin) {
            Admin admin = (Admin)principalCollection.getPrimaryPrincipal();
            // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            // 当前后台管理用户的权限集合
            info.addStringPermissions(admin.getApiPermStrlist());
            return info;
        } else if(principal instanceof User) {
            User user = (User)principalCollection.getPrimaryPrincipal();
            // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            // 统一的app用户的权限集合
            info.addStringPermissions(appApiPermNames);
            return info;
        }
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }
}
