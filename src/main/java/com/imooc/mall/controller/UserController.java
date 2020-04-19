package com.imooc.mall.controller;

import com.imooc.mall.controller.consts.MallConst;
import com.imooc.mall.form.UserLoginForm;
import com.imooc.mall.form.UserRegisterForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.UserServiceImpl;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController

@Slf4j
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/user/register")
    public ResponseVo register(@Valid  @RequestBody UserRegisterForm userRegisterForm){
        //表单验证
//        if(StringUtils.isEmpty(user.getUsername())){
//        }
//        if(){
//            如果按照这种方式验证的话，会很冗长。加入要验证很多参数，就又要加很多if语句判断。
//            所以创建userForm类，里面只定义需要验证的变量
//
//        }
        //登录信息判断(因为有统一的表单验证功能，所以可以不加这一段判断了，同时把方法里的第二个参数BindingResult bindingResult删掉)
//        //表单验证的方式
//        if (bindingResult.hasErrors()){
//            //获得的信息是UserForm的@NotBlank的信息
//            //bindingResult.getFieldError().getField()获取发生错误的字段
//            //bindingResult.getFieldError().getDefaultMessage()获取发生错误的字段后对应的显示信息
//            log.error("注册提交的参数有误，{}{}", Objects.requireNonNull(bindingResult.getFieldError()).getField(),bindingResult.getFieldError().getDefaultMessage());
//           // return ResponseVo.error(PARAM_ERROR);//如果只这样返回，在网页上只会提示参数错误，不能清楚的知道哪个参数出错了，所以需要和上一行一样
//          // 具体做法是在ResponseVo再重载一个error方法,下面这样写虽然可行，但是还是太难看，太长。所以再重载一个error方法
//            //return ResponseVo.error(PARAM_ERROR,bindingResult.getFieldError().getField()+" "+bindingResult.getFieldError().getDefaultMessage());
//            //再一次重载error方法后的使用
//            return ResponseVo.error(PARAM_ERROR,bindingResult);
//        }
//        //System.out.println()是输出到页面，不要这么用
//        log.info("username={}",userForm.getUsername());
//        //return ResponseVo.success();//测试ResponseVo.success()一下
//        //return ResponseVo.error(ResponseEnum.NEED_LOGIN);//测试ResponseVo.error()方式一
//        return ResponseVo.error(NEED_LOGIN);//测试ResponseVo.error()方式二，将其import进来

        User user=new User();
        BeanUtils.copyProperties(userRegisterForm,user);//对象之间copy的方法，把userform的内容copy给user里
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid  @RequestBody UserLoginForm userLoginForm,  HttpSession httpSession){
        //登录信息判断(因为有统一的表单验证功能，所以可以不加这一段判断了，同时把方法里的第二个参数BindingResult bindingResult删掉)
//        if (bindingResult.hasErrors()){
//            //log.error("注册提交的参数有误，{}{}", Objects.requireNonNull(bindingResult.getFieldError()).getField(),bindingResult.getFieldError().getDefaultMessage());
//            return ResponseVo.error(PARAM_ERROR,bindingResult);
//        }
        //从数据库拿用户信息
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        //设置Session,这是key-value形式 第一个参数是key,但是最好不要以下面这种方式写，所以可以新建个常量包。
        //将用户信息set进来
        httpSession.setAttribute(MallConst.CURRENT_USER,userResponseVo.getData());
        //把Cookie打印出来
        log.info("/user/login httpSessionId={}",httpSession.getId());//和cookie一模一样
        return userResponseVo;
    }
    //这种接口是按照和前端约定的接口文件来设计的
    //因为/User 和/User/ 是不一样的，为了使得接口一致，所以不能在该类不能加@RequestMapping("/user")，同时需要修改上面每个函数的地址
    //session保存在内存里，缺点是当服务器重启session就会没有了。所以一般我们会把它存储在redis里，又想让利用getId()就能找到对应的信息，所以需要token。
    //session的改进版方式是采用 token+redis（本课程不讲）
    @GetMapping("/user")//在用户登录成功之后就能获得用户信息。实际上已经携带了信息，所以可以把信息返回
    //postman直接是看不到的。在postman点击code查看。之所以后端知道它是登陆状态靠的是code里的Cookie: JSESSIONID=...
    //上行的Cookie是可以打印出来的。
    //cookie注意跨域。注意localhost是域名，127.0.0.1是Ip地址。这两个就算是跨域，所以这两个携带的cookie值是不一样的
    //session安全，cookie不安全。但是session和cookie是不分家的。在postman已经登陆好之后获得的cookie复制到在浏览器打开的user里的cookie。这样浏览器也会显示出信息。所以说cookie不安全
    public ResponseVo<User> userInfo(HttpSession httpSession){
        log.info("/user httpSessionId={}",httpSession.getId());//和cookie一模一样
       User user=(User) httpSession.getAttribute(MallConst.CURRENT_USER);
//       //下面注释掉的判断是否登录在拦截器里
//       如果没有登录，那么user是空的
//       if (user==null){
//           return ResponseVo.error(ResponseEnum.NEED_LOGIN);
//       }
       //登陆成功
        return ResponseVo.success(user);
    }

    //TODO 判断登陆状态，拦截器（因为以后有很多接口会需要判断登入状态。所以希望做个拦截器统一判断）
    @PostMapping("/user/logout")
    public  ResponseVo logout(HttpSession httpSession){
        log.info("/user/logout httpSessionId={}",httpSession.getId());//和cookie一模一样
        //下面注释掉的代码在拦截器里
//        User user=(User) httpSession.getAttribute(MallConst.CURRENT_USER);
//        //如果没有登录，那么user是空的。判断是否是登陆状态
//        if (user==null){
//            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
//        }
        //登出的操作就是移除session
        httpSession.removeAttribute(MallConst.CURRENT_USER);
        /*
         * 前端->java
         * cookie(SeesionId) ->session
         * 登陆失效情况：1、cookie里的SeesionId改变或者被删除 2、session因为存放在内存里，如果项目重启或者服务器重启 3、session过期
         *session过期看其配置。在.yml里配置  如果不配置的话，默认过期时间是30分钟。本课程改成10s 是不会生效的。因为有个类设置了最小生存时间是1分钟
         *这个类是 { @Link TomcatServletWebServerFactory} 里的getSessionTimeoutInMinutes函数设置了最小值。所以把配置文件改成两分钟，即120秒
         */
        return ResponseVo.success();
    }
}
