package com.imooc.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class OrderServiceImplTest extends MallApplicationTests {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private CartService cartService;

    private Gson gson=new GsonBuilder().setPrettyPrinting().create();
    private Integer uid=1;
    private Integer productId=26;
    private Integer shippingId=5;

    @Before
    public void before(){
        log.info("[新增购物车...]");
        CartAddForm cartAddForm=new CartAddForm();
        cartAddForm.setProductId(productId);
        cartAddForm.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(uid, cartAddForm);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    private ResponseVo<OrderVo> create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("result={}",gson.toJson(responseVo));
        return responseVo;
    }

    @Test
    public void createTest() {
        ResponseVo<OrderVo> responseVo = create();
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = orderService.list(uid, 1,10);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    @Test
    public void detail() {
        ResponseVo<OrderVo> orderVo=create();
        ResponseVo<OrderVo> responseVo = orderService.detail(uid, orderVo.getData().getOrderNo());
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    @Test
    public void cancel() {
        ResponseVo<OrderVo> orderVo=create();
        ResponseVo<OrderVo> responseVo = orderService.cancel(uid, orderVo.getData().getOrderNo());
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }
}