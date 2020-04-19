package com.imooc.mall.service.impl;

import com.google.gson.Gson;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.pojo.Cart;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.ICartService;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class CartService implements ICartService {
    private final static String CART_REDIS_KEY_TEMPLATE="cart_%d";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Gson gson=new Gson();
    @Override
    public ResponseVo<CartVo> add(Integer uid,CartAddForm cartAddForm) {
        Product product= productMapper.selectByPrimaryKey(cartAddForm.getProductId());
        Integer quantity=1;
        //判断商品是否存在
        if (product==null){
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }
        //判断商品是否是在售状态
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFFSALE_OR_DELETE);
        }
        //判断商品库存是否充足(接口文件说购物车数量是按1来添加的,所以只用判断商品库存是否大于0)
        if (product.getStock()<=0){
            return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
        //写入到Redis
        //key:cart_uid(eg:cart_1) 可以定义一个常量，且格式化
        redisTemplate.opsForValue().set(String.format(CART_REDIS_KEY_TEMPLATE,uid),gson.toJson(new Cart(product.getId(),quantity,cartAddForm.getSelected())));    //key-value形式

        return null;
    }
}
