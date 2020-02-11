package com.mycloud.usermanage.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mycloud.usermanage.entity.User;
import com.mycloud.usermanage.mapper.UserMapper;
import com.mycloud.usermanage.pojo.BaseResponse;
import com.mycloud.usermanage.pojo.ExtraParamResponse;
import com.mycloud.usermanage.shiro.UserType;
import com.mycloud.usermanage.util.RandomUtil;
import com.mycloud.usermanage.util.ShiroLogin;
import com.mycloud.usermanage.util.ShiroSession;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserMapper userMapper;

    @Value("${weixin.appid}")
    private String appID = "";

    @Value("${weixin.appsecret}")
    private String appsecret = "";

    public BaseResponse login(User param) {
        BaseResponse response = ShiroLogin.login(param.getName(), param.getPswd(), UserType.APP_USER);
        if(response.isSuccess()) {
            User user = userMapper.queryUser(param.getName(), UserType.APP_USER.ordinal(), 0);
            return loginSuccess(user, (String)((ExtraParamResponse)response).extra, UserType.APP_USER);
        } else {
            return response;
        }
    }

    public BaseResponse wxlogin(String authCode, String nickname, String headimgurl, Integer gender) {
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        /*通过code获取access_token和用户的openid(只需要SCOPE为snsapi_base),返回的数据:
        {
            "access_token":"ACCESS_TOKEN",
            "expires_in":7200,
            "refresh_token":"REFRESH_TOKEN",
            "openid":"OPENID",
            "scope":"snsapi_userinfo"
        }*/
        String data = doHttpGet(accessTokenUrl.replace("APPID", appID).replace("SECRET", appsecret).replace("CODE", authCode));
        JSONObject jsonData = JSONObject.fromObject(data);
        if (jsonData.has("openid")) {
            String openid = jsonData.getString("openid");

            User weixinUser = new User();
            if(nickname == null && headimgurl == null) {
                //微信第三方登录
                String accessToken = jsonData.getString("access_token");
                String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
                /*用access_token和用户的openid获取用户的信息(需要SCOPE为snsapi_userinfo),返回的用户信息:
                {
                    "openid":"OPENID",
                    "nickname":"",
                    "sex":1,
                    "language":"zh_CN",
                    "city":"",
                    "province":"",
                    "country":"",
                    "headimgurl":"",
                    "privilege":[]
                }*/
                String userInfo = doHttpGet(userInfoUrl.replace("OPENID", openid).replace("ACCESS_TOKEN", accessToken));

                jsonData = JSONObject.fromObject(userInfo);
                weixinUser.setNickname(jsonData.getString("nickname"));
                weixinUser.setAvatarUrl(jsonData.getString("headimgurl"));
                weixinUser.setSex(jsonData.getInt("sex"));//1男 2女
            } else {
                //微信小程序登录
                weixinUser.setNickname(nickname);
                weixinUser.setAvatarUrl(headimgurl);
                weixinUser.setSex(gender);
            }

            User user = userMapper.queryUser(openid, UserType.WX_USER.ordinal(), 0);
            if(user == null) {
                weixinUser.setWxOpenid(openid);
                weixinUser.setPswd(openid);
                userMapper.insertSelective(weixinUser);

                user = weixinUser;
            } else if(!Objects.toString(user.getNickname(),"").equals(weixinUser.getNickname()) ||
                      !Objects.toString(user.getAvatarUrl(),"").equals(weixinUser.getAvatarUrl())) {
                weixinUser.setId(user.getId());
                userMapper.updateByPrimaryKeySelective(weixinUser);

                user.setNickname(weixinUser.getNickname());
                user.setAvatarUrl(weixinUser.getAvatarUrl());
                user.setSex(weixinUser.getSex());
            }

            BaseResponse response = ShiroLogin.login(openid, openid, UserType.WX_USER);
            if(response.isSuccess()) {
                return loginSuccess(user, (String)((ExtraParamResponse)response).extra, UserType.WX_USER);
            } else {
                return response;
            }
        } else {
            return BaseResponse.loginFailed("未获得openid");
        }
    }

    public BaseResponse mlogin(User param) {
        String code = RandomUtil.randomNumeric(4);
        User user = userMapper.queryMobileUser(param.getMobile());
        if(user == null) {
            User userAdd = new User();
            userAdd.setMobile(param.getMobile());
            userAdd.setPswd(param.getMobile());
            userAdd.setCode(code);
            userMapper.insertSelective(userAdd);
        } else {
            user.setCode(code);
            userMapper.updateByPrimaryKeySelective(user);
        }

        sendSMS(param.getMobile(), "template", code);
        return BaseResponse.SUCCESS;
    }

    public BaseResponse mloginStep2(User param) {
        User user = userMapper.queryMobileUser(param.getMobile());
        if(user != null) {
            if(StringUtils.isNotBlank(user.getCode()) && user.getCode().equals(param.getCode())) {
                BaseResponse response = ShiroLogin.login(param.getMobile(), param.getMobile(), UserType.MOBILE_USER);
                if(response.isSuccess()) {
                    return loginSuccess(user, (String)((ExtraParamResponse)response).extra, UserType.MOBILE_USER);
                } else {
                    return response;
                }
            } else {
                return BaseResponse.loginFailed("验证码错误");
            }
        } else {
            return BaseResponse.loginFailed("手机号不存在");
        }
    }
    
    private BaseResponse loginSuccess(User user, String token, UserType userType) {
        user.token = token;
        ShiroSession.setCurrentUserId(user.getId());

        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setLastLoginTime(new Date());
        if(userType == UserType.MOBILE_USER) {
            userUpdate.setCode("");
        }
        userMapper.updateByPrimaryKeySelective(userUpdate);

        return new ExtraParamResponse<>(user);
    }
    
    private void sendSMS(String mobile, String template, String code) {
        
    }
    
    public BaseResponse queryList(User param, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);

        List<User> list = userMapper.queryList(param);
        return new ExtraParamResponse<>(new PageInfo(list));
    }

    public BaseResponse queryById(Integer id) {
        //仅限后台管理员和app或微信登录用户本人可以调用的接口
        Integer adminId = ShiroSession.getCurrentAdminId();
        if(adminId == null) {
            Integer userId = ShiroSession.getCurrentUserId();
            if(!userId.equals(id)) {
                return BaseResponse.UNAUTHORIZED;
            }
        }

        User user = userMapper.selectByPrimaryKey(id);
        if(user != null) {
            user.setPswd(null);
        }
        return new ExtraParamResponse<>(user);
    }

    public BaseResponse add(User param) {
        return new ExtraParamResponse<>(userMapper.insertSelective(param));
    }

    public BaseResponse update(User param) {
        if(param.oldPswd != null) {
            User user = userMapper.selectByPrimaryKey(param.getId());
            if(!user.getPswd().equals(param.oldPswd)) {
                return BaseResponse.invalidParam("原密码错误!");
            } else if(StringUtils.isBlank(param.getPswd())) {
                return BaseResponse.invalidParam("密码不能设置为空!");
            } else if(param.getPswd().length() < 6) {
                return BaseResponse.invalidParam("密码长度不能小于6位!");
            }
        }
        return new ExtraParamResponse<>(userMapper.updateByPrimaryKeySelective(param));
    }

    public BaseResponse delete(Integer id) {
        User param = new User();
        param.setId(id);
        param.setDeleteTime(new Date());
        return new ExtraParamResponse<>(userMapper.updateByPrimaryKeySelective(param));
    }

    private String doHttpGet(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        String resultBody = null;
        try {
            org.apache.http.HttpResponse response = client.execute(httpGet);
            org.apache.http.HttpEntity entity = response.getEntity();
            resultBody = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpGet.releaseConnection();
        }
        return resultBody;
    }
}
