package com.order.service.order_service.service;

import com.order.service.order_service.model.request.OrderRequest;
import com.order.service.order_service.model.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);

    OrderResponse getOrderById(Integer id);

    Page<OrderResponse> getAllOrders(LocalDateTime from, LocalDateTime to, String status, Pageable pageable);

    List<OrderResponse> getOrdersByUserId(Integer userId);

    OrderResponse updateOrderById(Integer id, OrderRequest orderRequest);

    void deleteOrderById(Integer id);
}
