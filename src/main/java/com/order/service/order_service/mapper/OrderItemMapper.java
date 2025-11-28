package com.order.service.order_service.mapper;

import com.order.service.order_service.model.dto.OrderItemDto;
import com.order.service.order_service.model.entity.OrderItem;
import com.order.service.order_service.model.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ItemMapper.class},
        imports = {Status.class}
)
public interface OrderItemMapper {

    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(OrderItemDto orderItem);

    OrderItemDto toOrderItemDto(OrderItem orderItem);
}
