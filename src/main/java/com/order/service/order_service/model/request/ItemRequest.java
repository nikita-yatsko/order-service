package com.order.service.order_service.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequest {

    @NotBlank(message = "Name can not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30")
    private String name;

    @NotNull(message = "Price can not be null")
    @Min(value = 0, message = "Price can not be less than 0")
    private Double price;
}
