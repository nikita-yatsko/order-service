package com.order.service.order_service.kafka.service;

import com.order.service.order_service.kafka.model.OrderStatus;
import com.order.service.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderService orderService;

    @RetryableTopic(attempts = "3")
    @KafkaListener(
            topics = "CREATE_PAYMENT",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrderStatus(
            OrderStatus orderStatus)
    {
        if (orderStatus.getOrderId() == null || orderStatus.getOrderStatus() == null) {
            log.error("Received null OrderStatus");
            return;
        }

        int orderId, userId;

        try {
            orderId = Integer.parseInt(orderStatus.getOrderId());
            userId = Integer.parseInt(orderStatus.getUserId());
        } catch (NumberFormatException e) {
            log.error("Invalid orderId or userId format");
            return;
        }

        log.info("Received order payment with orderId: {}; status: {}", orderId, orderStatus.getOrderStatus());
        orderService.setPaidStatus(orderId, userId, orderStatus.getOrderStatus());
    }
}
