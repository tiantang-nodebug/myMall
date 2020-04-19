package com.imooc.mall.dao;

import com.imooc.mall.pojo.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectAll();//在xml文件里因为没有输入参数，只查status=1的记录

}