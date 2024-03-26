package com.gugugu.haochat.common.exception.handler;

import com.gugugu.haochat.common.exception.BusinessException;
import com.gugugu.haochat.common.exception.NecessaryFieldsIsEmptyException;
import com.gugugu.haochat.common.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BusinessExceptionHandler {

    /**
     * 业务异常
     *
     * @param e BusinessException
     * @return ResultUtil<Boolean>
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResultUtil<Boolean> businessException(BusinessException e) {
        log.info("Business message: {}", e.getMessage());
        return ResultUtil.error(e.getMessage(), false);
    }

    /**
     * 必要字段为空异常
     *
     * @param e NecessaryFieldsIsEmptyException
     * @return ResultUtil<Boolean>
     */
    @ExceptionHandler(value = NecessaryFieldsIsEmptyException.class)
    public ResultUtil<Boolean> necessaryFieldsIsEmptyException(NecessaryFieldsIsEmptyException e) {
        log.info("Business message: {}", e.getMessage());
        return ResultUtil.error(e.getMessage(), false);
    }

}
