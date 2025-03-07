package com.chiu.naishuawangnextbackend.common;

import com.chiu.naishuawangnextbackend.exception.ErrorCode;
import lombok.Data;

/**
 * 全局响应封装类
 * @param <T>
 */
@Data
public class BaseResponse<T> {
    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message = "";
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage());
    }
}
