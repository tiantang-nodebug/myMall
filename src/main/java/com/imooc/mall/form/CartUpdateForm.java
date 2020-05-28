package com.imooc.mall.form;

import lombok.Data;

@Data
public class CartUpdateForm {
    //由于是非必填，所以表单不用校验
    private Integer quantity;
    private Boolean selected;

}
