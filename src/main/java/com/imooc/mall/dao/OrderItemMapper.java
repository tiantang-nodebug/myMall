package com.imooc.mall.dao;

import com.imooc.mall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    int batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);//批量写入
//--       (#{item})  批量写入时一定要把属性全部写出来，不然会出错

    List<OrderItem> selectByOrderNoSet(@Param("orderNoSet") Set<Long> orderNoSet);

}