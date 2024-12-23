package com.sspu.nslike.exception;

import org.springframework.http.HttpStatus;

/**
 * 自定义异常类
 * 用于处理业务逻辑中的特定异常情况
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
public class CustomException extends RuntimeException {
    /**
     * 错误代码
     */
    private int code;

    /**
     * 构造函数
     * @param message 错误信息
     * @param code 错误代码
     */
    public CustomException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误代码
     * @return 返回错误代码
     */
    public int getCode() {
        return code;
    }
}
