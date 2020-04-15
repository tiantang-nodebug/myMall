package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class CategoryMapperTest extends MallApplicationTests {

    @Autowired
    private CategoryMapper categoryMapper;
    @Test
    public void findById() {
        Category category = categoryMapper.findById(100001);//在该行小括号后面加.var回车就是快速赋值，是IDEA的功能
        System.out.println(category.toString());
    }

    @Test
    public void queryById() {
        Category category = categoryMapper.queryById(100001);//在该行小括号后面加.var回车就是快速赋值，是IDEA的功能
        System.out.println(category.toString());
    }
}