package com.order.service.order_service.model.exception;

public class DataExistException extends RuntimeException {
    public DataExistException(String message) {
        super(message);
    }
}
