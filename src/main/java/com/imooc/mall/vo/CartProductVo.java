package com.imooc.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartProductVo {
    private Integer productId;
    private Integer quantity;//购物车里的数量
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;//商品单价
    private Integer productStatus;
    private BigDecimal productTotalPrice;//商品单价乘以数量
    private Integer productStock;
    private Boolean productSelected;//商品是否选中
}
