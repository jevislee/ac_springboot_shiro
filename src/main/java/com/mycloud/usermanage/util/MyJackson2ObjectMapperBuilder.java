package com.mycloud.usermanage.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class MyJackson2ObjectMapperBuilder implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
        jacksonObjectMapperBuilder.failOnUnknownProperties(false);
        jacksonObjectMapperBuilder.dateFormat(DateUtil.yyyyMMddHHmmssFormat);
        jacksonObjectMapperBuilder.timeZone("GMT+8");
        //将对象的大写转换为下划线加小写，例如：userName-->user_name
//        jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }
}
