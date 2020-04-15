package com.imooc.mall.dao;


import com.imooc.mall.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;



/**
 * 创建操作数据据库的dao
 */
//@Mapper//当有很多张表，要写很多@Mapper。其实不必这样。可以把他注释掉，然后在主类里添加
@Component
//CategoryMapper只是一个普通的接口，要想实现查数据库的功能还需加一个注解，见上上一行@Mapper
public interface CategoryMapper {
    //使用mybatis注解
    //Select注解的用法（它的包名是org.apache.ibatis.annotations）
    @Select("select * from mall_category where id = #{id}")
    Category findById(@Param("id") Integer id);

    //使用xml方式
    Category queryById(Integer id);
}
