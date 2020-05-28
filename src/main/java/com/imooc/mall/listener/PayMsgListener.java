package com.imooc.mall.listener;

import com.google.gson.Gson;
import com.imooc.mall.pojo.PayInfo;
import com.imooc.mall.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues="payNotify")
public class PayMsgListener {

    @Autowired
    private OrderServiceImpl orderService;

    @RabbitHandler
    public void process(String msg){
        log.info("【接收到消息】=> {}",msg);
        /*
        把接收的消息转换格式,从json转换成payInfo对象。
        因为项目没有，需要新建payInfo对象(不是正确操作，因为payInfo就应该是属于pay模块而不应该在mall模块里出现)
        正确姿势：pay项目提供client.jar,mall引用这个jar包，而不需要新建payInfo。这种做法离不开多模块
        所以呢，还是采用了简单但是逻辑不应该的做法，复制payInfo对象
         */
        PayInfo payInfo = new Gson().fromJson(msg, PayInfo.class);
        //要判断订单状态是否是已支付。但是支付状态也是在pay模块里，如果要复制的话，内容复制的太多，这种耦合性太强不好。所以硬编码，就是把判断条件写死
        if (payInfo.getPlatformStatus().equals("SUCCESS")){
            //修改订单里的状态（目前orderService里没有修改订单状态的方法，所以要新写该方法）
            orderService.paid(payInfo.getOrderNo());
        }

    }
}
