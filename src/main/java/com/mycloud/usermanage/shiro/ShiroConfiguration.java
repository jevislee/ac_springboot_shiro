package com.mycloud.usermanage.shiro;

import com.mycloud.usermanage.mapper.ApiMapper;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * shiro配置项,usermanage的shiro只负责往redis存权限信息,网关的shiro只负责检查权限信息
 */
@Configuration
public class ShiroConfiguration {

    @Value("${crazycake.redis.host}")
    private String host;

    @Value("${crazycake.redis.port}")
    private Integer port;

    @Value("${crazycake.redis.password}")
    private String password;

    @Value("${crazycake.redis.expire}")
    private Integer expire;

    @Autowired
    private ApiMapper apiMapper;

    //自定义Realm
    @Bean
    public MyShiroRealm shiroRealm() {
        MyShiroRealm realm = new MyShiroRealm();
        return realm;
    }

    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setPassword(password);
        redisManager.setExpire(expire);// 配置缓存(用户token)过期时间,单位秒
        return redisManager;
    }

    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    @Bean("redisSessionDAO")
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    //自定义sessionManager
    @Bean
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();
        mySessionManager.setSessionDAO(redisSessionDAO());
        return mySessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        // 登录的用户访问页面首次验证角色权限时会把查询出来的角色权限信息缓存到redis,角色权限信息即为key,无value
        // key的ttl由crazycake.redis.expire参数指定, 用对应的sessionId访问页面*不会*恢复ttl
        securityManager.setCacheManager(cacheManager());
        // 用户每一次登录成功都会创建新的session缓存到redis,如果只允许同一账户单个登录则需要自己清除用户之前的session 
        // key为"shiro_redis_session:" + sessionId,value为用户信息和setAttribute设置的信息
        // key的ttl由crazycake.redis.expire参数指定, 每次用对应的sessionId访问页面都会恢复ttl,用户退出时会从redis删除对应的session和角色权限信息
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //没配置的资源默认允许访问
        Map<String, String> filterChainDefinitionManager = new LinkedHashMap<>();
        List<String> apis = apiMapper.queryApiUri();
        for(String api : apis) {
            System.out.println("perms[" + api + "]");
            filterChainDefinitionManager.put(api, "perms[" + api + "]");
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionManager);
        
        shiroFilterFactoryBean.setLoginUrl("/unlogined");//提示未登录
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");//提示权限不够(已登录状态)

        return shiroFilterFactoryBean;
    }

    /**
     * 开启shiro aop注解支持 @RequiresPermissions,@RequiresRoles,@RequiresUser
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }

    /**
     * shiro aop注解需要该方法
     *
     * setProxyTargetClass的值设定为true，就是以cglib动态代理方式生成代理类，设置为false，就是默认用JDK动态代理技术
     * JDK动态代理是通过接口中的方法名，在动态生成的代理类中调用业务实现类的同名方法
     * CGlib动态代理是通过继承业务类，生成的动态代理类是业务类的子类，通过重写业务方法进行代理
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }
}
