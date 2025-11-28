package com.order.service.order_service.mapper;

import com.order.service.order_service.model.dto.OrderInfo;
import com.order.service.order_service.model.dto.UserInfo;
import com.order.service.order_service.model.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderResponseMapper {

    OrderResponse toOrderResponse(UserInfo order, OrderInfo orderInfo);
}
