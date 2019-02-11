package com.mail.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json时，如果是null的对象，key也会消失
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private  T data;

    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status,T data){
        this.status=status;
        this.data= data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.data = data;
        this.msg = msg;
    }
    private ServerResponse(int status,String msg){
        this.msg = msg;
        this.status = status;
    }
    @JsonIgnore
    //使之不在json序列化中
    public boolean isSuccess(){
        return this.status == ResponseCode.Success.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.Success.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMsg(String msg){
        return new ServerResponse<T>(ResponseCode.Success.getCode(),msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.Success.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.Success.getCode(),msg,data);
    }
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }
    public static <T> ServerResponse<T> createByErrorMsg(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMsg);
    }
    public static <T> ServerResponse<T> createByError(int errorCode,String errorMSg){
        return new ServerResponse<T>(errorCode,errorMSg);
    }

}
