package com.imooc.mall;

import com.imooc.mall.controller.consts.MallConst;
import com.imooc.mall.exception.LoginException;
import com.imooc.mall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javax.security.auth.login.LoginException;

/**
 * 因为本课程拦截器少所以就直接放在了根目录下，当拦截器多的时候就需要新建个包，便于管理
 * 判断登陆状态，拦截器（因为以后有很多接口会需要判断登入状态。所以希望做个拦截器统一判断）
 * 有两种方式判断登陆状态
 * 1、Interceptor -Url  拦截器这种方式简单
 *
 * 2、AOP-包名   这种方式功能更强大
 */
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {
    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return true表示继续流程 false表示中断
     * @throws Exception
     * 拦截写完之后，需要配置。它是基于url配置的
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       log.info("preHandle...");
        User user=(User) request.getSession().getAttribute(MallConst.CURRENT_USER);
        //如果没有登录，那么user是空的。判断是否是登陆状态
        if (user==null){
            log.info("user=null");
            //return false;//如果只返回false 那么前端被拦截后是不会显示错误信息的。所以可以抛一个异常（之前写过的异常）
            //return ResponseVo.error(ResponseEnum.NEED_LOGIN);//因为该函数返回值是bool类型，所以这种返回结果不对，但是希望得到这种结果，所以抛异常
            throw new LoginException();//抛了异常之后会被异常处理拦截，要在异常处理里面捕获
        }
        return true;
    }
}
