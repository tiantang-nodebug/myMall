package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.enums.RoleEnum;
import com.imooc.mall.pojo.User;
import com.imooc.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional//做单测不希望把测试数据加入到数据库里
public class UserServiceImplTest extends MallApplicationTests {

    //为了方便写成静态变量
    public static final String USERNAME="jack";
    public static final String PASSWORD="jack";


    @Autowired
    private UserServiceImpl userServiceImpl;//因为IUserService只有一个实现类，所以可以这么写
    @Before
    public void register() {
        User user=new User(USERNAME,PASSWORD,"27281@qq.com", RoleEnum.CUSTOMER.getCode());

        userServiceImpl.register(user);
    }

    @Test
    public void login(){
        //register();//登陆之前需要注册。如果有很多其他要测试的方法，那都要写一遍register() 太麻烦。所以在register把@Test注解改成@Before就可以解决
        ResponseVo<User> login = userServiceImpl.login(USERNAME, PASSWORD);
        //判断是否登陆成功
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),login.getStatus());

    }
}