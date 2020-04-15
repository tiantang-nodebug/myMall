package com.imooc.mall.vo;//返回给前端的对象习惯称为vo对象 view object

import com.fasterxml.jackson.annotation.JsonInclude;
import com.imooc.mall.enums.ResponseEnum;
import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@Data
//@JsonSerialize(include = )  这个include已经被废弃了
@JsonInclude(value =JsonInclude.Include.NON_NULL )//这样就会把是null的数据去除，不显示
public class ResponseVo<T> {
    private Integer status;
    private String msg;
    private T data;//泛型，因为显示的data数据结构可能不是固定的

    public ResponseVo(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    //"成功"的静态方法
    public static <T> ResponseVo<T> success(String msg){
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(),msg);//这时不仅会显示status，msg还会显示data=null。如果不想显示data=null，就要加注释@JsonInclude

    }
    public static <T> ResponseVo<T> success(){
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getDesc());
    }
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum){
        return new ResponseVo<>(responseEnum.getCode(),responseEnum.getDesc());
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum,String msg){
        return new ResponseVo<>(responseEnum.getCode(),msg);
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, BindingResult bindingResult){
        return new ResponseVo<>(responseEnum.getCode(), Objects.requireNonNull(bindingResult.getFieldError()).getField()+" "+bindingResult.getFieldError().getDefaultMessage());
    }

}
