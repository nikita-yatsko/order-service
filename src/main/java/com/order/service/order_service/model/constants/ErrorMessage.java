package com.order.service.order_service.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorMessage {

    ITEM_NOT_FOUND_BY_ID("Item with ID: %s was not found"),
    ITEM_ALREADY_EXISTS("Item with name: %s already exists"),

    ORDER_NOT_FOUND_BY_ID("Order with ID: %s was not found"),
    INVALID_ORDER_ID_OR_USER_ID("Invalid order ID: %s or user ID: %s"),
    ;

    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
