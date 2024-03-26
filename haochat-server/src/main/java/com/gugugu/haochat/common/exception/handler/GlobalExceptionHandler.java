package com.gugugu.haochat.common.exception.handler;

import com.gugugu.haochat.common.utils.ResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 系统错误默认消息
     */
    public static final String SYSTEM_ERROR_MESSAGE = "系统出了点小差错，请稍后再试哦~~";

    /**
     * 处理所有不可知的异常
     *
     *
     * @return ResponseEntity
     */
    @ExceptionHandler(value = Exception.class)
    public ResultUtil<Boolean> handleException() {
        return ResultUtil.error(SYSTEM_ERROR_MESSAGE, false);
    }
}
