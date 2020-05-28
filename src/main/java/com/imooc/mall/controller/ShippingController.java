package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.controller.consts.MallConst;
import com.imooc.mall.form.ShippingForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.ShippingServiceImpl;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@RestController
public class ShippingController {
    @Autowired
    private ShippingServiceImpl shippingService;

    @PostMapping("/shippings")
    public ResponseVo<Map<String,Integer>> add(@Valid @RequestBody ShippingForm shippingForm, HttpSession httpSession){
        User user=(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        //对表单进行校验,在shippingForm类里加注解@NotBlank
        return shippingService.add(user.getId(),shippingForm);
    }

    @DeleteMapping("/shippings/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId, HttpSession httpSession){
        User user=(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        //对表单进行校验,在shippingForm类里加注解@NotBlank
        return shippingService.delete(user.getId(),shippingId);
    }

    @PutMapping("/shippings/{shippingId}")
    public ResponseVo update(@PathVariable Integer shippingId, HttpSession httpSession,@Valid @RequestBody ShippingForm shippingForm){
        User user=(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        //对表单进行校验,在shippingForm类里加注解@NotBlank
        return shippingService.update(user.getId(),shippingId,shippingForm);
    }

    @GetMapping("/shippings")
    public ResponseVo<PageInfo> list(@RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                     @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                     HttpSession httpSession){
        User user=(User) httpSession.getAttribute(MallConst.CURRENT_USER);
        //对表单进行校验,在shippingForm类里加注解@NotBlank
        return shippingService.list(user.getId(),pageNum,pageSize);
    }
}
