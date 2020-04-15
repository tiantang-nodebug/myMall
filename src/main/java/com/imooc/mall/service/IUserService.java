package com.imooc.mall.service;

import com.imooc.mall.pojo.User;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.stereotype.Component;

@Component
public interface IUserService {
    /**
     * 注册
     */
    ResponseVo register(User user);

    /**
     * 登录
     */
}
