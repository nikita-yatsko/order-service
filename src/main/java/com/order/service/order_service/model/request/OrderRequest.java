package com.order.service.order_service.model.request;

import com.order.service.order_service.model.dto.OrderItemDto;
import com.order.service.order_service.model.enums.Status;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "User ID can not be null")
    private Integer userId;

    @Min(value = 0, message = "Total price can not be less than 0")
    private Double totalPrice;

    @Column(nullable = false)
    private Status status;

    private List<OrderItemDto> items = new ArrayList<>();
}
