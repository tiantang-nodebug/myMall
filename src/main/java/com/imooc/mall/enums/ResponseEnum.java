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
        ;
    Integer code;
    String desc;

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
