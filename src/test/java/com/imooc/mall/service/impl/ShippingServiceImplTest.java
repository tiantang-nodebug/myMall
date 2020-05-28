package com.imooc.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.ShippingForm;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
public class ShippingServiceImplTest extends MallApplicationTests {

    @Autowired
    private ShippingServiceImpl shippingService;

    private Gson gson=new Gson();

    //除了把下面的属性定义还可以以另外的方式见下面的注释掉的代码
    private Integer uid=1;
    private Integer shippingId;
    //第一种方式  ，这种呢因为测试其他的方法之前都需要先运行add所以，在add前把@Test改成@Before
    //private ShippingForm shippingForm=new ShippingForm("廖师兄","010","18688888888","湖北","北京市","海淀区","中关村","100000");

    //第二种方式，这种呢因为测试其他的方法之前都需要先运行add所以，在add前把@Test删掉，变成普通成员方法，在before里调用add方法
    //用这种方式，shippingId是在add方法运行之后产生的，需要从add里面调用
    private ShippingForm shippingForm;
    @Before
    public void before(){
        ShippingForm shippingForm=new ShippingForm();
        shippingForm.setReceiverName("廖师兄");
        shippingForm.setReceiverPhone("010");
        shippingForm.setReceiverMobile("18688888888");
        shippingForm.setReceiverProvince("北京");
        shippingForm.setReceiverCity("北京市");
        shippingForm.setReceiverDistrict("海淀区");
        shippingForm.setReceiverAddress("中关村");
        shippingForm.setReceiverZip("100000");
        this.shippingForm=shippingForm;
        add();
    }
    public void add() {
        ResponseVo<Map<String, Integer>> responseVo = shippingService.add(uid, shippingForm);
        log.info("result={}",gson.toJson(responseVo));
        this.shippingId=responseVo.getData().get("ShippingId");//shippingId赋值，这样方便其他方法运行
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    @After
    public void delete() {
        ResponseVo responseVo = shippingService.delete(uid, shippingId);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    @Test
    public void update() {
        shippingForm.setReceiverCity("美国");
        ResponseVo responseVo = shippingService.update(uid, shippingId, shippingForm);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = shippingService.list(uid, 1, 10);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());//判断是否成功
    }
}