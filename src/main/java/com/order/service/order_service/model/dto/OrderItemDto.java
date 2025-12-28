package com.order.service.order_service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Data;


@Data
public class OrderItemDto {

    private Integer id;

    @Valid
    private ItemDto item;

    @Min(value = 1, message = "Quantity can not be less than 1")
    private Integer quantity;

}
