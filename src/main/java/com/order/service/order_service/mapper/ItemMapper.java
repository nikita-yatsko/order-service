package com.order.service.order_service.mapper;

import com.order.service.order_service.model.dto.ItemDto;
import com.order.service.order_service.model.entity.Item;
import com.order.service.order_service.model.request.ItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {OrderItemMapper.class}
)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Item createItem(ItemRequest itemRequest);

    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Item toItem(ItemDto item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateItem(ItemRequest itemRequest, @MappingTarget Item item);

    ItemDto toItemDto(Item item);
}
