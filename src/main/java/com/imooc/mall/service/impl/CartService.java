package com.imooc.mall.service.impl;

import com.google.gson.Gson;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.pojo.Cart;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.ICartService;
import com.imooc.mall.vo.CartProductVo;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class CartService implements ICartService {
    private final static String CART_REDIS_KEY_TEMPLATE="cart_%d";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
        //HashOperations<String, Object, Object> 把第二个参数改成String，这是因为1、String用的比较多，2、用的是StringRedisTemplate。把第三个参数改成String
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        //下面的代码使得再次添加商品时，购物车里的商品数量加1
        String rediskey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(rediskey, String.valueOf(product.getId()));
      //  Cart cart = new Cart();//可以这样写
        Cart cart;//也可以这样写
        if (StringUtils.isEmpty(value)){
            //redis没有该商品那么新增该商品
            cart=new Cart(product.getId(), quantity, cartAddForm.getSelected());
        }else{
            //redis有该商品那么就把数量加一(如果有那么是一个json格式,因为它对应是value)
            cart = gson.fromJson(value,Cart.class);
            cart.setQuantity(cart.getQuantity()+quantity);
        }
//        redisTemplate.opsForValue()  //之前写的key和value是一一对应 后来改成map结构 key和value是一对多
        opsForHash.put(rediskey,String.valueOf(product.getId()),gson.toJson(cart));    //key-value形式

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        //获取购物车列表
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String rediskey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(rediskey);
        CartVo cartVo=new CartVo();
        List<CartProductVo> cartProductVoList=new ArrayList<>();

        boolean selectAll=true;
        Integer cartTotalQuantity=0;
        BigDecimal cartTotalPrice=BigDecimal.ZERO;

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(),Cart.class);

            //之前说过，不能把查询sql放在for循环里
            //TODO 需要优化，使用mysql里的in（课程不做，让自己做）
            Product product=productMapper.selectByPrimaryKey(productId);
            if (product!=null){
                CartProductVo cartProductVo=new CartProductVo( productId,cart.getQuantity(),
                        product.getName(),product.getSubtitle() , product.getMainImage() ,
                        product.getPrice() ,product.getStatus() ,product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())) ,
                        product.getStock(), cart.getProductSelected());
                cartProductVoList.add(cartProductVo);
                if (!cart.getProductSelected()){
                    selectAll=false;
                }else{
                    //计算购物车总价（只计算选中的）
                    cartTotalPrice=cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                }
                cartTotalQuantity+=cart.getQuantity();
            }
        }
        cartVo.setSelectAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String rediskey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(rediskey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)){
            //redis没有该商品那么数据有问题，需要报错
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST_IN_CART);
        }else{
            //redis有该商品那么修改内容
            Cart cart = gson.fromJson(value,Cart.class);
            if (cartUpdateForm.getQuantity()!=null && cartUpdateForm.getQuantity()>=0){
                cart.setQuantity(cartUpdateForm.getQuantity());
            }
            if (cartUpdateForm.getSelected()!=null){
                cart.setProductSelected(cartUpdateForm.getSelected());
            }
            opsForHash.put(rediskey,String.valueOf(productId),gson.toJson(cart));
            return list(uid);
        }
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String rediskey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(rediskey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)){
            //redis没有该商品那么数据有问题，需要报错
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST_IN_CART);
        }else{
            //redis有该商品那么删除该商品
            opsForHash.delete(rediskey,String.valueOf(productId));
            return list(uid);
        }
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        //遍历购物车列表，把对应的物品的select状态全改成true.因为经常需要
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String rediskey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        //在redis里遍历查询，因为redis性能高，不用担心它遍历多次速度慢
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(rediskey,String.valueOf(cart.getProductId()),gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        //遍历购物车列表，把对应的物品的select状态全改成true.因为经常需要
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String rediskey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        //在redis里遍历查询，因为redis性能高，不用担心它遍历多次速度慢
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(rediskey,String.valueOf(cart.getProductId()),gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum = listForCart(uid).stream().map(Cart::getQuantity).reduce(0, Integer::sum);
        return ResponseVo.success(sum);
    }

    public List<Cart> listForCart(Integer uid){
        //遍历购物车列表，把对应的物品的select状态全改成true.因为经常需要
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String rediskey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(rediskey);
        List<Cart> cartList=new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(),Cart.class));
        }
        return cartList;
    }
}
