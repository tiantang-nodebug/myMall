package com.imooc.mall.dao;

import com.imooc.mall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface ShippingMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByIdAndUid(@Param("uid") Integer uid,@Param("shippingId")  Integer shippingId);

    List<Shipping> selectByUid(Integer uid);

    Shipping  selectByUidAndshippingId(@Param("uid") Integer uid,@Param("shippingId") Integer shippingId);

    List<Shipping> selectByIdSet(@Param("idSet") Set idSet);
}