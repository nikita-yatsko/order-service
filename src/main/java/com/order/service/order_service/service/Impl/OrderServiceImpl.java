package com.order.service.order_service.service.Impl;

import com.order.service.order_service.mapper.OrderMapper;
import com.order.service.order_service.model.dto.OrderDto;
import com.order.service.order_service.model.entity.Order;
import com.order.service.order_service.model.enums.Status;
import com.order.service.order_service.model.request.OrderRequest;
import com.order.service.order_service.repository.OrderRepository;
import com.order.service.order_service.service.OrderService;
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
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(OrderRequest orderRequest) {
        Double totalPrice = orderRequest.getItems()
                .stream()
                .mapToDouble(item -> item.getItem().getPrice())
                .sum();
        orderRequest.setTotalPrice(totalPrice);

        Order newOrder = orderRepository.save(orderMapper.createOrder(orderRequest));
        return orderMapper.toOrderDto(newOrder);
    }

    @Override
    public OrderDto getOrderById(Integer id) {
        return orderRepository.findOrderById(id)
                .map(orderMapper::toOrderDto)
                .orElseThrow(() -> new RuntimeException("Order with id " + id + " not found"));
    }

    @Override
    public Page<OrderDto> getAllOrders(LocalDateTime from, LocalDateTime to, Status status, Pageable pageable) {
        Specification<Order> specification = OrderSpecification.dateBetween(from, to);
        Page<Order> orders = orderRepository.findAll(specification, pageable);

        return orders.map(orderMapper::toOrderDto);
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Integer userId) {
        return orderRepository.findAllOrdersByUserId(userId)
                .stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto updateOrderById(Integer id, OrderRequest orderRequest) {
        Order order = orderRepository.findOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order with id " + id + " not found"));
        orderMapper.updateOrder(orderRequest, order);

        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void deleteOrderById(Integer id) {
        if (orderRepository.findOrderById(id).isEmpty())
            throw new RuntimeException("Order not found");

        orderRepository.softDelete(id);
    }
}
