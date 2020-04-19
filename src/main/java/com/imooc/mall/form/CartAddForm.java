package com.imooc.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 添加商品时候的表单
 */
@Data
public class CartAddForm {

    @NotNull
    private Integer productId;

    private Boolean selected=true;//商品是否选中(给默认值)
}
