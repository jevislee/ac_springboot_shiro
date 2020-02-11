package com.mycloud.usermanage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * 访问地址 http://localhost:8080/swagger-ui.html
 * 
 * controller注解
 * @Api(tags = "used at class")
 * @ApiOperation(value = "used at method", produces="application/json")
 * @ApiParam(value = "used at param of method")
 * 
 * pojo注解
 * @ApiModel(value = "used at class")
 * @ApiModelProperty(value = "used at property field")
 */
@Configuration //spring boot配置注解
@EnableSwagger2 //启用swagger2功能注解
public class Swagger2 {
    @Bean
    public Docket createRestfulApi() {//api文档实例     
        List<Parameter> pars = new ArrayList<>();
        pars.add(new ParameterBuilder().name("token").description("token").modelRef(new ModelRef("string")).parameterType("header").required(false).build());

        return new Docket(DocumentationType.SWAGGER_2)//文档类型：DocumentationType.SWAGGER_2
                .apiInfo(apiInfo())//api信息
                .select()//构建api选择器
                .apis(RequestHandlerSelectors.basePackage("com.mycloud"))//api选择器选择api的包
                .paths(PathSelectors.any())//api选择器选择包路径下任何api显示在文档中
                .build()//创建文档
                .globalOperationParameters(pars)//设置全局header(比如token,这类经常在AOP里统一获取和校验的参数)
                .useDefaultResponseMessages(false)//不显示Unauthorized,Forbidden,Not Found这些Response描述
                ;
    }

    private ApiInfo apiInfo() {//接口的相关信息
        return new ApiInfoBuilder()
                .title("接口文档")//页面标题
                .version("1.0") //API版本号
                .build();
    }
}
