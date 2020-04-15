package com.imooc.mall.pojo;

import lombok.Data;

import java.util.Date;

/**
 * po(persistent object)持久层的对象。category那张表映射过来对应的对象就叫po
 * pojo(plian ordinary java object)普通的java对象
 */
@Data
public class Category {
    private Integer id;
    private  Integer parentId;
    private  String name;
    private Integer status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
}
