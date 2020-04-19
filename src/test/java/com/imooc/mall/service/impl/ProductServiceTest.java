package com.imooc.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceTest extends MallApplicationTests {
    @Autowired
    private ProductService productService;

    @Test
    public void list() {
       // productService.list(100002,1,1);//这个是为了测试能不能正确输出接口文件里的list
        ResponseVo<PageInfo> responseVo = productService.list(100002, 1, 1);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void detail(){
        ResponseVo<ProductDetailVo> responseVo = productService.detail(26);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}