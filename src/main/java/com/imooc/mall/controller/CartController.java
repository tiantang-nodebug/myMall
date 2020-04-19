package com.imooc.mall.controller;

import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CartController {

    /**
     * 购物车添加商品
     * @return
     */
    @PostMapping("/carts")
    public ResponseVo<CartVo>  add(@Valid @RequestBody CartAddForm cartAddForm){
        return null;
    }
}
