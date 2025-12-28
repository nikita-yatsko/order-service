package com.order.service.order_service.service;

import com.order.service.order_service.model.dto.OrderDto;
import com.order.service.order_service.model.enums.Status;
import com.order.service.order_service.model.request.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderRequest orderRequest);

    OrderDto getOrderById(Integer id);

    Page<OrderDto> getAllOrders(LocalDateTime from, LocalDateTime to, Status status, Pageable pageable);

    List<OrderDto> getOrdersByUserId(Integer userId);

    OrderDto updateOrderById(Integer id, OrderRequest orderRequest);

    void deleteOrderById(Integer id);
}
