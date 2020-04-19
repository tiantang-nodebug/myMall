package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.form.CartAddForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class CartServiceTest extends MallApplicationTests {

    @Autowired
    private CartService cartService;

    @Test
    public void add() {
        CartAddForm cartAddForm=new CartAddForm();
        cartAddForm.setProductId(26);
        cartAddForm.setSelected(true);
        cartService.add(1,cartAddForm);
    }
}