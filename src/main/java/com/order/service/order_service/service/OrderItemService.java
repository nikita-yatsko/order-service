package com.order.service.order_service.service;

import com.order.service.order_service.model.dto.OrderItemDto;

public interface OrderItemService {

    OrderItemDto create(OrderItemDto orderItemDt);

    OrderItemDto update(OrderItemDto orderItemDt);

    OrderItemDto delete(OrderItemDto orderItemDt);
}
