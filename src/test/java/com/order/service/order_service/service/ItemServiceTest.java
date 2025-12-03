package com.order.service.order_service.service;

import com.order.service.order_service.mapper.ItemMapper;
import com.order.service.order_service.model.constants.ErrorMessage;
import com.order.service.order_service.model.dto.ItemDto;
import com.order.service.order_service.model.entity.Item;
import com.order.service.order_service.model.exception.DataExistException;
import com.order.service.order_service.model.exception.NotFoundException;
import com.order.service.order_service.model.request.ItemRequest;
import com.order.service.order_service.repository.ItemRepository;
import com.order.service.order_service.service.Impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setId(1);
        item.setName("test");
        item.setPrice(100.0);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("test");
        itemDto.setPrice(100.0);
    }

    @Test
    public void getItemById_success() {
        // Arrange:
        when(itemRepository.findItemById(1)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // Act:
        ItemDto result = itemService.getItemById(1);

        // Assert:
        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getPrice(), result.getPrice());

        //Verify:
        verify(itemRepository, times(1)).findItemById(1);
        verify(itemMapper, times(1)).toItemDto(item);
    }

    @Test
    public void getItemById_throwError_notFound() {
        // Arrange:
        when(itemRepository.findItemById(1)).thenReturn(Optional.empty());

        // Act:
        NotFoundException result = assertThrows(NotFoundException.class, () -> itemService.getItemById(1));

        // Assert:
        assertNotNull(result);
        assertEquals(ErrorMessage.ITEM_NOT_FOUND_BY_ID.getMessage(1), result.getMessage());

        // Verify:
        verify(itemRepository, times(1)).findItemById(1);
    }

    @Test
    public void saveItem_success() {
        // Arrange
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setName("test");
        itemRequest.setPrice(10.0);

        Item mappedItem = new Item();
        mappedItem.setName("test");
        mappedItem.setPrice(10.0);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setPrice(10.0);

        when(itemRepository.findByName(itemRequest.getName())).thenReturn(Optional.empty());
        when(itemMapper.createItem(itemRequest)).thenReturn(mappedItem);
        when(itemRepository.save(mappedItem)).thenReturn(mappedItem);
        when(itemMapper.toItemDto(mappedItem)).thenReturn(itemDto);

        // Act
        ItemDto result = itemService.saveItem(itemRequest);

        // Assert
        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getPrice(), result.getPrice());

        // Verify
        verify(itemRepository, times(1)).findByName("test");
        verify(itemMapper, times(1)).createItem(itemRequest);
        verify(itemRepository, times(1)).save(mappedItem);
        verify(itemMapper, times(1)).toItemDto(mappedItem);
    }

    @Test
    public void saveItem_throwError_dataExistException() {
        // Arrange:
        ItemRequest request = new ItemRequest();
        request.setName("test");
        request.setPrice(10.0);

        when(itemRepository.findByName(request.getName())).thenReturn(Optional.of(item));

        // Act:
        DataExistException result = assertThrows(DataExistException.class, () -> itemService.saveItem(request));

        // Assert:
        assertNotNull(result);
        assertEquals(ErrorMessage.ITEM_ALREADY_EXISTS.getMessage(request.getName()), result.getMessage());

        // Verify:
        verify(itemRepository, times(1)).findByName(request.getName());
    }

    @Test
    public void getAllItems_success() {
        // Arrange:
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // Act:
        List<ItemDto> result = itemService.getAllItems();

        // Assert:
        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getFirst().getId());
        assertEquals(itemDto.getName(), result.getFirst().getName());
        assertEquals(itemDto.getPrice(), result.getFirst().getPrice());

        // Verify:
        verify(itemRepository, times(1)).findAll();
        verify(itemMapper, times(1)).toItemDto(item);
    }

    @Test
    public void updateItem_success() {
        // Arrange:
        Integer id = 1;

        ItemRequest request = new ItemRequest();
        request.setName("updated");
        request.setPrice(20.0);

        Item item = new Item();
        item.setId(id);
        item.setName("old");
        item.setPrice(10.0);

        Item updated = new Item();
        updated.setId(id);
        updated.setName("updated");
        updated.setPrice(20.0);

        ItemDto dto = new ItemDto();
        dto.setName("updated");
        dto.setPrice(20.0);

        when(itemRepository.findItemById(id)).thenReturn(Optional.of(item));
        when(itemRepository.findByName("updated")).thenReturn(Optional.empty());
        doNothing().when(itemMapper).updateItem(request, item);
        when(itemRepository.save(item)).thenReturn(updated);
        when(itemMapper.toItemDto(updated)).thenReturn(dto);

        // Act
        ItemDto result = itemService.updateItem(id, request);

        // Assert
        assertNotNull(result);
        assertEquals("updated", result.getName());
        assertEquals(20.0, result.getPrice());

        verify(itemRepository).findItemById(id);
        verify(itemRepository).findByName("updated");
        verify(itemMapper).updateItem(request, item);
        verify(itemRepository).save(item);
    }

    @Test
    public void updateItem_throwError_notFoundException() {
        // Arrange:
        Integer id = 1;

        ItemRequest request = new ItemRequest();
        request.setName("updated");
        request.setPrice(20.0);

        when(itemRepository.findItemById(id)).thenReturn(Optional.empty());

        // Act
        NotFoundException result = assertThrows(NotFoundException.class, () -> itemService.updateItem(id, request));

        // Assert
        assertNotNull(result);
        assertEquals(ErrorMessage.ITEM_NOT_FOUND_BY_ID.getMessage(1), result.getMessage());

        // Verify:
        verify(itemRepository, times(1)).findItemById(id);
    }

    @Test
    public void updateItem_throwError_dataExistException() {
        // Arrange:
        Integer id = 1;

        ItemRequest request = new ItemRequest();
        request.setName("test");
        request.setPrice(20.0);

        Item item = new Item();
        item.setId(id);
        item.setName("old");
        item.setPrice(10.0);

        when(itemRepository.findItemById(id)).thenReturn(Optional.of(item));
        when(itemRepository.findByName(request.getName())).thenReturn(Optional.of(item));

        // Act
        DataExistException result = assertThrows(DataExistException.class, () -> itemService.updateItem(id, request));

        // Assert
        assertNotNull(result);
        assertEquals(ErrorMessage.ITEM_ALREADY_EXISTS.getMessage(request.getName()), result.getMessage());

        // Verify:
        verify(itemRepository).findItemById(id);
        verify(itemRepository).findByName(request.getName());
    }

    @Test
    public void deleteItem_success() {
        // Arrange:
        when(itemRepository.findItemById(1)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(1);

        // Act:
        itemService.deleteItem(1);

        // Verify:
        verify(itemRepository, times(1)).findItemById(1);
        verify(itemRepository, times(1)).deleteById(1);
    }

    @Test
    public void deleteItem_throwError_notFoundException() {
        // Arrange:
        when(itemRepository.findItemById(1)).thenReturn(Optional.empty());

        // Act:
        NotFoundException result = assertThrows(NotFoundException.class, () -> itemService.deleteItem(1));

        // Assert:
        assertNotNull(result);
        assertEquals(ErrorMessage.ITEM_NOT_FOUND_BY_ID.getMessage(1), result.getMessage());

        // Verify:
        verify(itemRepository, times(1)).findItemById(1);
    }

}