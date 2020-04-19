package com.imooc.mall.vo;

import lombok.Data;

import java.util.List;

@Data
/**
 * 按照约定的接口文件来设计
 */
public class CategoryVo {
    private Integer id;
    private  Integer parentId;
    private  String name;
    private Integer sortOrder;
    private List<CategoryVo> subCategories;
}
