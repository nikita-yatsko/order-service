package com.order.service.order_service.service.Impl;

import com.order.service.order_service.mapper.OrderMapper;
import com.order.service.order_service.mapper.OrderResponseMapper;
import com.order.service.order_service.model.constants.ErrorMessage;
import com.order.service.order_service.model.dto.UserInfo;
import com.order.service.order_service.model.entity.Order;
import com.order.service.order_service.model.entity.OrderItem;
import com.order.service.order_service.model.exception.InvalidDataException;
import com.order.service.order_service.model.exception.NotFoundException;
import com.order.service.order_service.model.request.OrderRequest;
import com.order.service.order_service.model.response.OrderResponse;
import com.order.service.order_service.repository.OrderRepository;
import com.order.service.order_service.service.OrderService;
import com.order.service.order_service.service.UserCacheService;
import com.order.service.order_service.utils.specifications.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderResponseMapper orderResponseMapper;
    private final UserCacheService userCacheService;

    private final static String EMAIL = "admin@example.com"; //TODO idk how to implement it (where will we get email?)

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Double totalPrice = orderRequest.getItems()
                .stream()
                .mapToDouble(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();
        orderRequest.setTotalPrice(totalPrice);

        Order order = orderMapper.createOrder(orderRequest);
        for (OrderItem item : order.getOrderItems())
            item.setOrder(order);

        Order newOrder = orderRepository.save(order);
        UserInfo userInfo = userCacheService.getUserInfo(EMAIL);
        return orderResponseMapper.toOrderResponse(userInfo, orderMapper.toOrderDto(newOrder));
    }

    @Override
    public OrderResponse getOrderById(Integer id) {
        return orderRepository.findOrderById(id)
                .map(order -> orderResponseMapper.toOrderResponse(userCacheService.getUserInfo(EMAIL), orderMapper.toOrderDto(order)))
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(id)));
    }

    @Override
    public Page<OrderResponse> getAllOrders(LocalDateTime from, LocalDateTime to, String status, Pageable pageable) {
        Specification<Order> specification = Specification
                .anyOf(OrderSpecification.dateBetween(from, to))
                .and(OrderSpecification.filterByStatus(status));

        Page<Order> orders = orderRepository.findAll(specification, pageable);

        return orders.map(order -> orderResponseMapper.toOrderResponse(userCacheService.getUserInfo(EMAIL), orderMapper.toOrderDto(order)));
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        return orderRepository.findAllOrdersByUserId(userId)
                .stream()
                .map(order -> orderResponseMapper.toOrderResponse(userCacheService.getUserInfo(EMAIL), orderMapper.toOrderDto(order)))
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateOrderById(Integer id, OrderRequest orderRequest) {
        Order order = orderRepository.findOrderById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(id)));
        orderMapper.updateOrder(orderRequest, order);

        return orderResponseMapper.toOrderResponse(userCacheService.getUserInfo(EMAIL), orderMapper.toOrderDto(order));
    }

    @Override
    public Boolean isOwner(Integer orderId, Integer userId) {
        if (orderId == null || userId == null)
            throw new InvalidDataException(ErrorMessage.INVALID_ORDER_ID_OR_USER_ID.getMessage(orderId, userId));
        return orderRepository.findOrderById(orderId)
                .map(od -> od.getUserId().equals(userId))
                .orElse(false);
    }

    @Override
    @Transactional
    public void deleteOrderById(Integer id) {
        if (orderRepository.findOrderById(id).isEmpty())
            throw new NotFoundException(ErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(id));

        orderRepository.softDelete(id);
    }
}
