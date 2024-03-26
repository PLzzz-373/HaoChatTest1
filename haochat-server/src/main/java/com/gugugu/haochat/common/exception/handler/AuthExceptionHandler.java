package com.gugugu.haochat.common.exception.handler;

import com.gugugu.haochat.common.exception.UnAuthorizationException;
import com.gugugu.haochat.common.utils.ResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.gugugu.haochat.common.constant.GlobalConst.EMPTY_STR;

@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * 默认未授权异常信息
     */
    public static final String DEFAULT_UNAUTH_MESSAGE = "请先登录";

    /**
     * 处理未登录异常
     *
     * @param e Exception
     * @return ResponseEntity
     */
    @ExceptionHandler(value = UnAuthorizationException.class)
    public ResultUtil<Boolean> handleAuthException(UnAuthorizationException e) {
        if (EMPTY_STR.equals(e.getMessage())) {
            return ResultUtil.error(DEFAULT_UNAUTH_MESSAGE, false);
        }
        return ResultUtil.error(e.getMessage(), false);
    }
}
