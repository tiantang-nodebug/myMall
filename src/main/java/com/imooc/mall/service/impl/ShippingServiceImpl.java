package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.ShippingMapper;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.ShippingForm;
import com.imooc.mall.pojo.Shipping;
import com.imooc.mall.service.IShippingService;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    /**
     *
     * @param uid
     * @param shippingForm
     * @return 地址的id，这个id在数据库是自增的
     */
    @Override
    public ResponseVo<Map<String,Integer>> add(Integer uid, ShippingForm shippingForm) {
        Shipping shipping=new Shipping();
        BeanUtils.copyProperties(shippingForm,shipping);
        shipping.setUserId(uid);//设置uid
        int row = shippingMapper.insertSelective(shipping);
        //return 地址的id，这个id在数据库是自增的
        if (row==0){
            return ResponseVo.error(ResponseEnum.ERRPOR);//如果行号是0，那么就有问题
        }
        Map<String,Integer> map=new HashMap<>();
        map.put("ShippingId", shipping.getId());//直接shipping.getId()是拿不到的，需要在mybatis做一些配置
        return ResponseVo.success(map);
    }

    @Override
    public ResponseVo delete(Integer uid, Integer shippingId) {

        int row = shippingMapper.deleteByIdAndUid(uid, shippingId);
        if (row==0){
            //没有删除，删除失败
            return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm,shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row = shippingMapper.updateByPrimaryKeySelective(shipping);//注意把创建时间和更新时间的mysql语句删掉
        if (row==0){
            return ResponseVo.error(ResponseEnum.ERRPOR);//如果行号是0，那么就有问题
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings = shippingMapper.selectByUid(uid);
        PageInfo pageInfo=new PageInfo(shippings);
        return ResponseVo.success(pageInfo);
    }
}
