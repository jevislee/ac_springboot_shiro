package com.mycloud.usermanage.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 绑定协议响应封装
 */
@ApiModel(value = "服务端统一响应(带业务数据)")
@JsonPropertyOrder({ "code", "message", "extra" })
public class ExtraParamResponse<T> extends BaseResponse {

    @ApiModelProperty(value = "业务数据")
    @JsonProperty("data")
    public T extra;

    public ExtraParamResponse(){
        super();
    }

    public ExtraParamResponse(BaseResponse baseResponse, T extra) {
        super(baseResponse.getCode(), baseResponse.getMessage());
        this.extra = extra;
    }

    public ExtraParamResponse(T extra) {
        this(BaseResponse.SUCCESS, extra);
    }
}
