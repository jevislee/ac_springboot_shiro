package com.mycloud.usermanage.controller;

import com.mycloud.usermanage.entity.User;
import com.mycloud.usermanage.impl.UserServiceImpl;
import com.mycloud.usermanage.pojo.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags="app用户接口")
@RestController
public class AppUserController {

    @Autowired
    private UserServiceImpl userService;

    @ApiOperation(value = "用户名密码登录", produces="application/json")
    @PostMapping("/user/login")
    public BaseResponse login(@RequestBody User param) {
        return userService.login(param);
    }

    //获取微信用户信息的新接口(微信授权重定向写到前端,接收code的回调写到前端,前端把code传给后端接口进行后续处理并且等待该后端接口返回微信用户信息)
    //微信小程序登录需要传登录用户的nickname、headimgurl、gender,微信第三方登录是后端获取登录用户信息
    @ApiOperation(value = "微信授权登录", produces="application/json")
    @PostMapping("/user/wxlogin")
    public BaseResponse wxlogin(@RequestParam String authCode,
                              @RequestParam(required = false) String nickname,
                              @RequestParam(required = false) String headimgurl,
                              @RequestParam(required = false) Integer gender) {
       return userService.wxlogin(authCode, nickname, headimgurl, gender);
    }

    @ApiOperation(value = "发送手机号登录验证码", produces="application/json")
    @PostMapping("/user/mlogin")
    public BaseResponse mlogin(@RequestBody User param) {
        return userService.mlogin(param);
    }

    @ApiOperation(value = "完成手机号登录", produces="application/json")
    @PostMapping("/user/mloginStep2")
    public BaseResponse mloginStep2(@RequestBody User param) {
        return userService.mloginStep2(param);
    }
    
    @ApiOperation(value = "查询用户详情", produces="application/json")
    @GetMapping("/user/details")
    public BaseResponse queryById(@RequestParam Integer userId) {
        return userService.queryById(userId);
    }
}
