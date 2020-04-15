package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.enums.RoleEnum;
import com.imooc.mall.pojo.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional//做单测不希望把测试数据加入到数据库里
public class UserServiceImplTest extends MallApplicationTests {

    @Autowired
    private UserServiceImpl userServiceImpl;//因为IUserService只有一个实现类，所以可以这么写
    @Test
    public void register() {
        User user=new User("tang","163748","27281@qq.com", RoleEnum.CUSTOMER.getCode());

        userServiceImpl.register(user);
    }
}