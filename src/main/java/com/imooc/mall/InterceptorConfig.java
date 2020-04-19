package com.imooc.mall;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    //.addPathPatterns("/**")默认所有接口都要拦截。 .excludePathPatterns()里面添加不需要拦截的接口
    //如果不需要拦截的接口比较多，可以写到配置文件里
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/error","/user/login","/user/register","/categories","/products","/products/*","/carts");//"/products/{productId}"或"/products/*"都可以
    }
}
