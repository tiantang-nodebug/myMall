package com.imooc.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserForm {
//    @NotBlank  用于String判断空格
//    @NotEmpty  用于集合
//    @NotNull   判断是否为null
    //@NotBlank(message = "用户名不能为空")//如果不加（）那么打印出来的日志只会提示不能为空。有另一种解决办法是在UserController的log.error里再加一个参数
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
}
