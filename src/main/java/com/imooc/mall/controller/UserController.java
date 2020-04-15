package com.imooc.mall.controller;

import com.imooc.mall.form.UserForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.UserServiceImpl;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

import static com.imooc.mall.enums.ResponseEnum.PARAM_ERROR;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseVo register(@Valid  @RequestBody UserForm userForm, BindingResult bindingResult){
        //表单验证
//        if(StringUtils.isEmpty(user.getUsername())){
//        }
//        if(){
//            如果按照这种方式验证的话，会很冗长。加入要验证很多参数，就又要加很多if语句判断。
//            所以创建userForm类，里面只定义需要验证的变量
//
//        }
        //表单验证的方式
        if (bindingResult.hasErrors()){
            //获得的信息是UserForm的@NotBlank的信息
            //bindingResult.getFieldError().getField()获取发生错误的字段
            //bindingResult.getFieldError().getDefaultMessage()获取发生错误的字段后对应的显示信息
            log.error("注册提交的参数有误，{}{}", Objects.requireNonNull(bindingResult.getFieldError()).getField(),bindingResult.getFieldError().getDefaultMessage());
           // return ResponseVo.error(PARAM_ERROR);//如果只这样返回，在网页上只会提示参数错误，不能清楚的知道哪个参数出错了，所以需要和上一行一样
          // 具体做法是在ResponseVo再重载一个error方法,下面这样写虽然可行，但是还是太难看，太长。所以再重载一个error方法
            //return ResponseVo.error(PARAM_ERROR,bindingResult.getFieldError().getField()+" "+bindingResult.getFieldError().getDefaultMessage());
            //再一次重载error方法后的使用
            return ResponseVo.error(PARAM_ERROR,bindingResult);
        }
//        //System.out.println()是输出到页面，不要这么用
//        log.info("username={}",userForm.getUsername());
//        //return ResponseVo.success();//测试ResponseVo.success()一下
//        //return ResponseVo.error(ResponseEnum.NEED_LOGIN);//测试ResponseVo.error()方式一
//        return ResponseVo.error(NEED_LOGIN);//测试ResponseVo.error()方式二，将其import进来

        User user=new User();
        BeanUtils.copyProperties(userForm,user);//对象之间copy的方法，把userform的内容copy给user里
        return userService.register(user);
    }
}
