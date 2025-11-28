package com.order.service.order_service.mapper;

import com.order.service.order_service.model.dto.OrderDto;
import com.order.service.order_service.model.dto.OrderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderInfoMapper {

    OrderInfo toOrderInfo(OrderDto orderDto);
}
