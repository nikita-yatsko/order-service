package com.order.service.order_service.service.Impl;

import com.order.service.order_service.mapper.ItemMapper;
import com.order.service.order_service.model.exception.DataExistException;
import com.order.service.order_service.model.exception.NotFoundException;
import com.order.service.order_service.model.constants.ErrorMessage;
import com.order.service.order_service.model.dto.ItemDto;
import com.order.service.order_service.model.entity.Item;
import com.order.service.order_service.model.request.ItemRequest;
import com.order.service.order_service.repository.ItemRepository;
import com.order.service.order_service.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto getItemById(Integer id) {
        return itemRepository.findItemById(id)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ITEM_NOT_FOUND_BY_ID.getMessage(id)));
    }

    @Override
    @Transactional
    public ItemDto saveItem(ItemRequest itemRequest) {
        if (itemRepository.findByName(itemRequest.getName()).isPresent())
            throw new DataExistException(ErrorMessage.ITEM_ALREADY_EXISTS.getMessage(itemRequest.getName()));

        return itemMapper.toItemDto(itemRepository.save(itemMapper.createItem(itemRequest)));
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer id, ItemRequest itemRequest) {
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ITEM_NOT_FOUND_BY_ID.getMessage(id)));

        if (!item.getName().equals(itemRequest.getName()) && itemRepository.findByName(itemRequest.getName()).isPresent())
            throw new DataExistException(ErrorMessage.ITEM_ALREADY_EXISTS.getMessage(itemRequest.getName()));

        itemMapper.updateItem(itemRequest, item);
        Item updatedItem = itemRepository.save(item);

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Integer id) {
        if (itemRepository.findItemById(id).isEmpty())
            throw new NotFoundException(ErrorMessage.ITEM_NOT_FOUND_BY_ID.getMessage(id));

        itemRepository.deleteById(id);
    }
}
