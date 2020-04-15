package com.imooc.mall.dao;

import com.imooc.mall.pojo.User;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);//对应的.xml里返回的是整个记录，但是selectByUsername不需要

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int countByUsername(String username);//只需要返回count 就知道表里有没有这个用户名的记录

    int  countByEmail(String email);//只需要返回count 就知道表里有没有这个用户名的记录
}