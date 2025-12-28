package com.order.service.order_service.service;

import com.order.service.order_service.model.dto.ItemDto;
import com.order.service.order_service.model.request.ItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(Integer id);

    ItemDto saveItem(ItemRequest itemRequest);

    List<ItemDto> getAllItems();

    ItemDto updateItem(Integer id, ItemRequest itemRequest);

    void deleteItem(Integer id);
}
