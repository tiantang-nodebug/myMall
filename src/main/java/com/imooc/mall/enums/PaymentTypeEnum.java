package com.imooc.mall.enums;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {
    PAY_ONLINE(1),
    ;

    PaymentTypeEnum(Integer code) {
        this.code = code;
    }

    Integer code;


}
