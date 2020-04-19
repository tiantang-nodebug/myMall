package com.imooc.mall.exception;

import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@ControllerAdvice
public class RuntimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody//返回一个json格式
    //@ResponseStatus(HttpStatus.FORBIDDEN)//以后自己做项目可能会需要的扩展知识。这个是修改网页状态码。这个和一下的json数据没有关系
    public ResponseVo handle(RuntimeException e){

        return ResponseVo.error(ResponseEnum.ERRPOR,e.getMessage());
    }

    //捕获未登录的拦截器发出的异常
    @ExceptionHandler(LoginException.class)
    @ResponseBody//返回一个json格式
    public ResponseVo userLoginHandle(){
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo notValidExceptionHandle(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        Objects.requireNonNull(bindingResult.getFieldError());
        return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult.getFieldError().getField()+" "+bindingResult.getFieldError().getDefaultMessage());
    }

}
