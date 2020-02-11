package com.mycloud.usermanage.controller;

import com.mycloud.usermanage.pojo.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags="通用接口")
@RestController
public class CommonController {

    @ApiOperation(value = "退出登录", produces="application/json")
    @RequestMapping("/logout")
    public BaseResponse logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
        return BaseResponse.SUCCESS;
    }

    //必须设置为RequestMapping,否则PUT和DELETE无法跳转到该接口
    @ApiOperation(value = "提示未登录", produces="application/json")
    @RequestMapping("/unlogined")
    public BaseResponse unlogined() {
        //前端没有传token或者token失效时则返回,用于给前端自动跳转到登录页面
        return BaseResponse.UNLOGINED;
    }
    
    //必须设置为RequestMapping,否则PUT和DELETE无法跳转到该接口
    @ApiOperation(value = "提示无权限", produces="application/json")
    @RequestMapping("/unauthorized")
    public BaseResponse unauthorized() {
        //前端调用的接口没有相应的权限时则返回,用于给前端弹出无权限提示框
        return BaseResponse.UNAUTHORIZED;
    }
}
