package com.imooc.mall.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {
    ERRPOR(-1,"服务端异常"),
    SUCCESS(0,"成功"),
    PASSWORD_ERROR(1,"密码错误"),
    USERNAME_EXIT(2,"用户名已存在"),
    PARAM_ERROR(3,"参数错误"),
    USEREMAIL_EXIT(4,"用户邮箱已被注册"),
    NEED_LOGIN(10,"用户未登录，请先登录"),
    USERNAME_OR_PASSWORD_ERROR(11,"用户名或密码错误"),
    PRODUCT_OFFSALE_OR_DELETE(12,"商品下架或删除"),
    PRODUCT_NOT_EXIST(13,"商品不存在"),
    PRODUCT_STOCK_ERROR(14,"库存不足"),
    PRODUCT_NOT_EXIST_IN_CART(15,"购物车商品不存在"),
    DELETE_SHIPPING_FAIL(16,"删除收货地址失败"),
    SHIPPING_NOT_EXIST(17,"收货地址不存在"),
    CART_SELECTED_IS_EMPTY(18,"购物车里没有选中的商品，请选择商品后下单"),
    ORDER_NOT_EXIST(19,"该订单不存在"), //不能直接告诉***没有该订单，涉及数据敏感性问题
    ORDER_STATUS_ERROR(20,"订单状态有误"),
        ;
    Integer code;
    String desc;

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
