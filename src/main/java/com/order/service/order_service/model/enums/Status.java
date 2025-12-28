package com.order.service.order_service.model.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum Status {
    CREATED,
    PAID,
    IN_PROCESS,
    SHIPPED,
}
