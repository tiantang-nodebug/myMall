package com.imooc.mall.service.impl;

import com.imooc.mall.dao.UserMapper;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.enums.RoleEnum;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.IUserService;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;


@Component
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    /**
     * 注册
     *将数据写入数据库
     * 用户名，email不能重复
     * @param user
     */
    @Override
    public ResponseVo<User> register(User user) {
        //error();//见下面，为了模拟错误
        //用户名不能重复(需要在UserMapper加入按用户名查找的方法)
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0) {
            // throw new RuntimeException("该用户名已注册");//现在返回值不再为空而是ResponseVo，所以就不用再以这种方式抛出异常
            return ResponseVo.error(ResponseEnum.USERNAME_EXIT);
        }

        //邮箱不能重复(需要在UserMapper加入按邮箱查找的方法)
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByUsername > 0) {
            //throw new RuntimeException("该邮箱已注册");
            return ResponseVo.error(ResponseEnum.USEREMAIL_EXIT);
        }

        //密码MD5摘要算法（Spring里自带了MD5）
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setRole(RoleEnum.CUSTOMER.getCode());
        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            //throw new RuntimeException("注册失败");
            return ResponseVo.error(ResponseEnum.ERRPOR);
        }
        return ResponseVo.success();
    }



//    public void error(){
//        throw new RuntimeException("意外错误");//这样返回的报错信息和前端约定的不一样，这样会导致前端不能很好的获得需要的信息做出相应操作
//        //加一个异常的捕获，把异常都写在exception包里，之后完成保证输出格式是json格式
//    }

    /**
     * 登录
     *
     * @param username
     * @param password
     */
    @Override
    public ResponseVo<User> login(String username,String password) {
        //通过用户名查数据
        User user = userMapper.selectByUsername(username);//username是索引，所以只用username来查找就好
        if (user==null){
            //用户不存在，不要返回这个提示信息（这是一种安全措施，不能让人家知道数据库没有这个用户。一般都会提示用户名或密码错误）
            //用户名错误
            //提示用户名或者密码错误
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        if(!user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))){
            //密码错误
            //提示用户名或者密码错误
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("********");//这样直接返回会把用户的密码返回这样不好。所以需要把用户的密码隐藏,这是其中一种做法，还有一种做法是在User里加注解将其忽略掉（这种方法不推荐）
        return ResponseVo.success(user);
    }
}
