package com.order.service.order_service.model.dto;

import com.order.service.order_service.model.enums.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderInfo {

    @NotNull(message = "Status must be set")
    private Status status;

    @Min(value = 0, message = "Total price can not be less than 0")
    private Double totalPrice;

    @Valid
    private List<OrderItemDto> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
