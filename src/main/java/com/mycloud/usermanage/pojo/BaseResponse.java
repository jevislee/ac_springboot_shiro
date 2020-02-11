package com.mycloud.usermanage.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 通用响应封装
 */
@ApiModel(value = "服务端统一响应")
public class BaseResponse {

    public static final int CODE_SUCCESS = 1000;//操作成功
    public static final int CODE_INVALID_PARAM = 1001;//参数无效
    public static final int CODE_SYSTEM_BUSY = 1002;//系统繁忙(操作失败)
    public static final int CODE_LOGIN_FAILED = 1003;//登录失败
    public static final int CODE_UNLOGINED = 1004;//未登录
    public static final int CODE_UNAUTHORIZED = 1005;//无权限

    public static final BaseResponse SUCCESS = new BaseResponse(CODE_SUCCESS, "success");
    public static final BaseResponse SYSTEM_BUSY = new BaseResponse(CODE_SYSTEM_BUSY, "系统忙，请稍后再试");
    public static final BaseResponse UNLOGINED = new BaseResponse(CODE_UNLOGINED, "未登录");
    public static final BaseResponse UNAUTHORIZED = new BaseResponse(CODE_UNAUTHORIZED, "无权限");

    @ApiModelProperty(value = "响应码")
    protected Integer code;

    @ApiModelProperty(value = "响应信息")
    protected String message;

    public BaseResponse() {
        this(BaseResponse.SUCCESS);
    }

    public BaseResponse(BaseResponse value) {
        this(value.code, value.message);
    }

    public BaseResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static BaseResponse invalidParam(String reason) {
        return new BaseResponse(CODE_INVALID_PARAM, reason);
    }

    public static BaseResponse loginFailed(String reason) {
        return new BaseResponse(CODE_LOGIN_FAILED, reason);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return (this.code == CODE_SUCCESS);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof BaseResponse)) {
            return false;
        }

        if (((BaseResponse) obj).getCode() != this.getCode()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "BaseResponse [code=" + code + ", message=" + message + "]";
    }
}
