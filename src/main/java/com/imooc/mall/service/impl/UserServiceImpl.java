package com.imooc.mall.service.impl;

import com.imooc.mall.dao.UserMapper;
import com.imooc.mall.enums.ResponseEnum;
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
    public ResponseVo register(User user) {
        //用户名不能重复(需要在UserMapper加入按用户名查找的方法)
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername>0){
           // throw new RuntimeException("该用户名已注册");//现在返回值不再为空而是ResponseVo，所以就不用再以这种方式抛出异常
            return ResponseVo.error(ResponseEnum.USERNAME_EXIT);
        }

        //邮箱不能重复(需要在UserMapper加入按邮箱查找的方法)
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByUsername>0){
            //throw new RuntimeException("该邮箱已注册");
            return ResponseVo.error(ResponseEnum.USEREMAIL_EXIT);
        }

        //密码MD5摘要算法（Spring里自带了MD5）
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount==0){
            //throw new RuntimeException("注册失败");
            return ResponseVo.error(ResponseEnum.ERRPOR);
        }
        return ResponseVo.success();
    }
}
