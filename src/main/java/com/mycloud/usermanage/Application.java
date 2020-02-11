package com.mycloud.usermanage;

import com.mycloud.usermanage.util.MyJackson2ObjectMapperBuilder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication(scanBasePackages = {"com.mycloud"})
@MapperScan(basePackages = {"com.mycloud.usermanage.mapper"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOrigin("*");//允许所有来源域名或IP地址
        corsConfig.addAllowedHeader("*");//允许所有header字段
        corsConfig.addAllowedMethod("*");//允许所有请求方法
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfig);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return new MyJackson2ObjectMapperBuilder();
    }
}
