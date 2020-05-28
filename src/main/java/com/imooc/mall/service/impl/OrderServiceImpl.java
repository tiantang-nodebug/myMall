package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.OrderItemMapper;
import com.imooc.mall.dao.OrderMapper;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.dao.ShippingMapper;
import com.imooc.mall.enums.OrderStatusEnum;
import com.imooc.mall.enums.PaymentTypeEnum;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.pojo.*;
import com.imooc.mall.service.IOrderService;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.OrderItemVo;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ShippingMapper shippingMapper;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid,Integer shippingId) {
        //收货地址校验（是否存在该地址）（总之要查出来）
        //shippingMapper通过uid和shippingId来查找
        Shipping shipping = shippingMapper.selectByUidAndshippingId(uid, shippingId);
        if (shipping==null){
            //收货地址不存在
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }



        //通过uid获取购物车，校验（是否有该商品，是否有库存）
        //之前在CartService写过listForCart(之前是private方法，现在改成public)但是作者把该方法也加到了ICartService 然后在本类定义ICartService。其实不是很懂
        List<Cart> cartList = cartService.listForCart(uid).stream()
                .filter(Cart::getProductSelected).collect(Collectors.toList());//只用取出来选中了的商品
        //判断购物车是否有选中的商品
        if (CollectionUtils.isEmpty(cartList)){
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }
        //判断选中的商品库存是否充足
        //获取cartList里的productIds
        Set<Integer> productSet = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toSet());//因为这些商品一定会重复，所以可以用set
        //通过productMapper按productIds查找(在mapper新写该方法)
        //需要测试productSet=null的情况 productSet=new HashSet<>();
        List<Product> productsList = productMapper.selectByProductIdSet(productSet);
        Map<Integer,Product> map = productsList.stream().collect(Collectors.toMap(Product::getId,product -> product));
        List<OrderItem> orderItemList=new ArrayList<>();
        Long orderNo=generateOrderNo();
        for (Cart cart : cartList) {
//            //根据productId查询数据库（这是可以优化的，那个CartService里有TODO的任务和这里是一样的）
//            //优化做法是在for循环外查数据库（见for循环上面的代码）  通过productMapper一次性查出来
//             //想访问Product那么就要遍历productsList，所以可以用map来存，这样可以直接访问
            Product product = map.get(cart.getProductId());
            //是否有该商品
            if (product==null){
                //最好加入提示信息
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,"商品不存在,productId:"+cart.getProductId());
            }
            //商品的状态（在售，下架...）(在mysql语句查询时加了status=1即在售，现在要判断那么就要把该条件删除)
            if (!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())){
                return ResponseVo.error(ResponseEnum.PRODUCT_OFFSALE_OR_DELETE,product.getName()+"不是在售状态");
            }
            //库存是否充足
            if (product.getStock()<cart.getQuantity()){
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR,product.getName()+"库存不正确");
            }
            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);

            //减库存
            product.setStock(product.getStock()-cart.getQuantity());
            //该方法加注解@Transactional 那么该方法里任何一个操作数据库失败都会回滚，所以不用担心，减库存操作成功，下面的写入order或order_item表失败
            //@Transactional是数据库的，不是java的。那么回滚条件不是写入数据库或更新数据库失败。可以在@Transactional后加（rollbackFor 右键点进去看 。回滚条件默认是出现runTimeException）
            int rowForProduct = productMapper.updateByPrimaryKeySelective(product);
            if (rowForProduct<=0){
                return ResponseVo.error(ResponseEnum.ERRPOR);
            }

            /*
            更新购物车，不要在这里做，因为redis没有回滚。假如在遍历一个购物车里的商品时，
             */

        }
        //计算总价（只计算选中的商品）
        //生成订单，入库： order和orderItem  需要用到事务（需要两个表同步，要么都成功要么都失败）
        //事务保证order和orderi_tem两个表同时写入或没有写入  就是在该方法上加一个注解@Transactional
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);
        int rowForOrder = orderMapper.insertSelective(order);
        if (rowForOrder<=0){
            return ResponseVo.error(ResponseEnum.ERRPOR);
        }

//        for (OrderItem orderItem : orderItemList) {
//        //不要这样写，因为这样又会多次执行sql语句。可以批量写入，就是在mapper再写一个插入方法batchInsert
//        //写完之后测试一下
//            orderItemMapper.insertSelective(orderItem);
//        }
        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem<=0){
            return ResponseVo.error(ResponseEnum.ERRPOR);
        }


        /*
        //将在购物车里已经下了单的商品删掉 更新购物车（redis也有事务，但是和mysql的事务不一样，redis事务是打包命令，不能回滚。redis是单线程的）
         在保证上面步骤都成功的前提下再更新购物车
         不用多想会不会购物车有的商品没有更新，不然代码会写起来没完没了的
         */
        for (Cart cart : cartList) {
            ResponseVo<CartVo> delete = cartService.delete(uid, cart.getProductId());
        }


        //构造orderVo对象
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);

        return ResponseVo.success(orderVo);
    }




    private OrderVo buildOrderVo(Order order,List<OrderItem> orderItemList,Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo);

        List<OrderItemVo> orderItemVoList = orderItemList.stream().map(e -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(e, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVoList);

        //shipping可能会被删掉 所以需要判断以下。所以最好把shipping地址保存在order表里
        if (shipping!=null){
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }
        return orderVo;
    }

    private Order buildOrder(Integer uid,Long orderNo,Integer shippingId,List<OrderItem> orderItemList) {
        //setPayment总价是由orderItemList里的对象的总价累加和 BigDecimal.ZERO是为0的初始值
        BigDecimal payment = orderItemList.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);//因为本课程没有涉及运费，所以就设为0
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());//因为本课程只有在线支付，所以是未支付的状态
        //创建时间和更新时间由mysql处理
        return order;
    }

    /**
     * 企业级：分布式唯一id/主键
     * @return
     * 本课程介绍很简单的方法
     */
    private Long generateOrderNo() {
        //使用时间戳（毫秒） 加一个三位的随机数
        return System.currentTimeMillis()+new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer uid,Long orderNo,Integer quantity,Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(uid);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());//单价
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        //创建时间和更新时间由mysql处理
        return orderItem;
    }


    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {

        List<Order> orderList = orderMapper.selectByUid(uid);
        PageHelper.startPage(pageNum,pageSize);
        
        Set<Long> orderNoSet = orderList.stream().map(Order::getOrderNo)
                .collect(Collectors.toSet());
        Set<Integer> shippingIdSet = orderList.stream().map(Order::getShippingId).collect(Collectors.toSet());
        //用sql的in语句来查减少查询数据库的次数（个人认为是以空间换时间）
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        List<Shipping> shippingList = shippingMapper.selectByIdSet(shippingIdSet);

        //需要把orderItemList转换成map
        Map<Long,List<OrderItem>> orderItemMap = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderNo));
        Map<Integer,Shipping> shippingMap=shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId,shipping->shipping));

        List<OrderVo> orderVoList=new ArrayList<>();
        for (Order order : orderList) {
//            buildOrderVo(order,orderItemList,)   这里第二个参数不应该是orderItemList，因为orderItemList不是和order对应的
            //希望是一个map。通过orderNo可以得到它对应的List<OrderItem> 见上面
            //shippingId也是如此
            //为了代码逻辑的完整性，可以增加对orderItemMap.get(order.getOrderNo()的判断，若得不到结果那就是数据库的问题，可以返回提示信息
            OrderVo orderVo = buildOrderVo(order,
                    orderItemMap.get(order.getOrderNo()),
                    shippingMap.get(order.getShippingId()));

            orderVoList.add(orderVo);
        }
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        
        return ResponseVo.success(pageInfo);
    }



    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        Set<Long> orderNoSet=new HashSet<>();
        orderNoSet.add(order.getOrderNo());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //校验该订单是不是属于该用户
        if (order==null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        //设定只有未付款订单可以取消（视业务而定）
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row<=0){
            return ResponseVo.error(ResponseEnum.ERRPOR);
        }

        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //校验该订单是不是属于该用户
        if (order==null){
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc() +"订单号：" +orderNo);
        }
        //设定只有未付款订单可以修改订单状态为已支付(视需要的业务而定)
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc()+"订单号：" +orderNo);
        }
        //修改订单状态
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());//最好呢是在PayInfo里拿支付时间，但是PayInfo里没有这个字段，最好是在PayInfo里加入这个字段
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row<=0){
            throw new RuntimeException("将订单更新为已支付状态失败！"+"订单号：" +orderNo);
        }
    }


}
