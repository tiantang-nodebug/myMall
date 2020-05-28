package com.imooc.mall.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
//    '订单状态:0-已取消-
//    10-未付款，
//    20-已付款，
//    40-已发货，
//    50-交易成功，
//    60-交易关闭'
        CANCELED(0,"订单已取消"),
        NO_PAY(10,"订单未付款"),
        PAID(20,"订单已付款"),
        SHIPPED(40,"订单已发货"),
        TRADE_SUCCESS(50,"交易成功"),
        TRADE_CLOSED(60,"交易关闭"),

        ;

    Integer code;
    String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
