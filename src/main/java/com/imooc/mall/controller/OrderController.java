package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.controller.consts.MallConst;
import com.imooc.mall.dao.OrderItemMapper;
import com.imooc.mall.form.OrderCreateForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.OrderServiceImpl;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 入参虽然只有一个参数shippingId但是也是json对象，需要单独写一个Form
     * @return
     */
    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(@Valid  @RequestBody OrderCreateForm orderCreateForm, HttpSession httpSession){
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        ResponseVo<OrderVo> responseVo = orderService.create(user.getId(), orderCreateForm.getShippingId());
        return responseVo;
    }

    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     HttpSession httpSession){
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        ResponseVo<PageInfo>  responseVo= orderService.list(user.getId(), pageNum,pageSize);
        return responseVo;
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable Long orderNo,
                                     HttpSession httpSession){
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        ResponseVo<OrderVo>  responseVo= orderService.detail(user.getId(), orderNo);
        return responseVo;
    }

    @PutMapping("/orders/{orderNo}")
    public ResponseVo cancel(@PathVariable Long orderNo,
                                      HttpSession httpSession){
        User user =(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        ResponseVo  responseVo= orderService.cancel(user.getId(), orderNo);
        return responseVo;
    }
}
