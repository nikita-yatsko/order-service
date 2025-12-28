package com.order.service.order_service.mapper;

import com.order.service.order_service.model.dto.OrderDto;
import com.order.service.order_service.model.entity.Order;
import com.order.service.order_service.model.enums.Status;
import com.order.service.order_service.model.request.OrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ItemMapper.class},
        imports = {Status.class}
)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(Status.CREATED)")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(source = "items", target = "orderItems")
    Order createOrder(OrderRequest order);

    @Mapping(target = "items", source = "orderItems")
    OrderDto toOrderDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "items", target = "orderItems")
    void updateOrder(OrderRequest orderRequest, @MappingTarget Order order);

    Order toOrder(OrderDto orderDto);
}
