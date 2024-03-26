package com.gugugu.haochat.common.exception.handler;

import com.gugugu.haochat.common.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ValidationExceptionHandler {

    /**
     * 字段验证异常
     *
     * @param e 异常
     * @return 异常体信息
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultUtil<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> message.append(error.getField()));
        return ResultUtil.error(message);
    }
}
