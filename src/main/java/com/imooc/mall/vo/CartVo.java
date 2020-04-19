package com.imooc.mall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 */
@Data
public class CartVo {
    private List<CartProductVo> cartProductVoList;
    private Boolean selectAll;//是否把购物车商品全选
    private BigDecimal cartTotalPrice;//购物车商品总价
    private Integer cartTotalQuantity;

}
