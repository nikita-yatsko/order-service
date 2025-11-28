package com.order.service.order_service.model.response;

import com.order.service.order_service.model.dto.OrderInfo;
import lombok.Data;

@Data
public class OrderResponse {

    private String name;
    private String surname;
    private String email;

    private OrderInfo orderInfo;
}
