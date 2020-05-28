package com.imooc.mall.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CartServiceTest extends MallApplicationTests {

    @Autowired
    private CartService cartService;

    private Gson gson=new GsonBuilder().setPrettyPrinting().create();

    private Integer uid=1;
    private Integer productId=27;

    @Before
    public void add() {
        log.info("[新增购物车...]");
        CartAddForm cartAddForm=new CartAddForm();
        cartAddForm.setProductId(productId);
        cartAddForm.setSelected(true);

        ResponseVo<CartVo> responseVo = cartService.add(uid, cartAddForm);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    @Test
    public void list(){
        ResponseVo<CartVo> list = cartService.list(uid);//为了看结果，需要打印一下,但是这样打印是一行，所以要转换成Json格式
        log.info("result={}",gson.toJson(list));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),list.getStatus());
    }

    @Test
    public void update(){
        CartUpdateForm cartUpdateForm=new CartUpdateForm();
        cartUpdateForm.setQuantity(5);
        cartUpdateForm.setSelected(false);
        ResponseVo<CartVo> responseVo = cartService.update(uid, productId, cartUpdateForm);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());


    }

    @After
    public void delete(){
        log.info("[删除购物车...]");
        ResponseVo<CartVo> responseVo = cartService.delete(uid, productId);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }

    @Test
    public void selectAll(){
        ResponseVo<CartVo> responseVo = cartService.selectAll(uid);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }

    @Test
    public void unSelectAll(){
        ResponseVo<CartVo> responseVo = cartService.unSelectAll(uid);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }

    @Test
    public void sum(){
        ResponseVo<Integer> responseVo = cartService.sum(uid);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }

}