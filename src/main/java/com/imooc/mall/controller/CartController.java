package com.imooc.mall.controller;

import com.imooc.mall.controller.consts.MallConst;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.CartService;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     *
     * @param httpSession
     * @return
     */
    @GetMapping("/carts")
    public ResponseVo<CartVo>  list(HttpSession httpSession){
        //从session里面获取uid
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        return cartService.list(user.getId());
    }

    /**
     * 购物车新增商品
     * @return
     */
    @PostMapping("/carts")
    public ResponseVo<CartVo>  add(@Valid @RequestBody CartAddForm cartAddForm, HttpSession httpSession){
        //从session里面获取uid
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        return cartService.add(user.getId(),cartAddForm);
    }

    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo>  update(@PathVariable Integer productId, @Valid @RequestBody CartUpdateForm cartUpdateForm, HttpSession httpSession){
        //从session里面获取uid
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        return cartService.update(user.getId(),productId,cartUpdateForm);
    }

    @DeleteMapping("/carts/{productId}")
    public ResponseVo<CartVo>  delete(@PathVariable Integer productId, HttpSession httpSession){
        //从session里面获取uid
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        return cartService.delete(user.getId(),productId);
    }
    @PutMapping("/carts/selectAll")
    public ResponseVo<CartVo>  selectAll(HttpSession httpSession){
        //从session里面获取uid
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        return cartService.selectAll(user.getId());
    }

    @PutMapping("/carts/unSelectAll")
    public ResponseVo<CartVo>  unSelectAll(HttpSession httpSession){
        //从session里面获取uid
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        return cartService.unSelectAll(user.getId());
    }

    @GetMapping("/carts/products/sum")
    public ResponseVo<Integer>  sum(HttpSession httpSession){
        //从session里面获取uid
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        return cartService.sum(user.getId());
    }
}
