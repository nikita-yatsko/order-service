package com.order.service.order_service.controllers;

import com.order.service.order_service.model.request.OrderRequest;
import com.order.service.order_service.model.response.OrderResponse;
import com.order.service.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(orderService.getAllOrders(from, to, status, pageable));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<OrderResponse>> getOrderByUserId(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(id));
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable("id") Integer id,
            @RequestBody @Valid OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.updateOrderById(id, orderRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}
