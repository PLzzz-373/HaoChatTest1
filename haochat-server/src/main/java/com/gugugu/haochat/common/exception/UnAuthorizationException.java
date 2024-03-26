package com.gugugu.haochat.common.exception;

public class UnAuthorizationException extends RuntimeException {

    public UnAuthorizationException() {
    }

    public UnAuthorizationException(String message) {
        super(message);
    }
}
