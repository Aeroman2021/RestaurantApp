package com.restaurant.app.demo.controller;

import java.sql.Timestamp;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        Object error,
        Timestamp date
) {

    public  static <T> ApiResponse<T> ok(T t,String message){
        return new ApiResponse<>(true,t,message,null,new Timestamp(System.currentTimeMillis()));
    }

    public  static ApiResponse<?> error(String message,Object error){
        return new ApiResponse<>(false,null,message,error,new Timestamp(System.currentTimeMillis()));
    }

}
