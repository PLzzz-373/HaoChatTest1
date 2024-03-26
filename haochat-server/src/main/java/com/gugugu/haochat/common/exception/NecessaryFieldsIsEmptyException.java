package com.gugugu.haochat.common.exception;

public class NecessaryFieldsIsEmptyException extends RuntimeException{

    public NecessaryFieldsIsEmptyException() {
    }

    public NecessaryFieldsIsEmptyException(String message) {
        super(message);
    }
}
