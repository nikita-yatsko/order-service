package com.order.service.order_service.service;

import com.order.service.order_service.mapper.OrderMapper;
import com.order.service.order_service.mapper.OrderResponseMapper;
import com.order.service.order_service.model.constants.ErrorMessage;
import com.order.service.order_service.model.dto.ItemDto;
import com.order.service.order_service.model.dto.OrderDto;
import com.order.service.order_service.model.dto.OrderItemDto;
import com.order.service.order_service.model.dto.UserInfo;
import com.order.service.order_service.model.entity.Order;
import com.order.service.order_service.model.entity.OrderItem;
import com.order.service.order_service.model.enums.Status;
import com.order.service.order_service.model.exception.NotFoundException;
import com.order.service.order_service.model.request.OrderRequest;
import com.order.service.order_service.model.response.OrderResponse;
import com.order.service.order_service.repository.OrderRepository;
import com.order.service.order_service.service.Impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderResponseMapper orderResponseMapper;

    @Mock
    private UserCacheService userCacheService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private OrderItemDto orderItemDto;

    private OrderItem orderItem;
    private Order order;
    private OrderDto orderDto;

    private UserInfo userInfo;
    private OrderResponse orderResponse;

    private ItemDto item;

    @BeforeEach
    public void setUp() {
        item = new ItemDto();
        item.setId(1);
        item.setName("test");
        item.setPrice(100.0);

        userInfo = new UserInfo();
        userInfo.setName("John");
        userInfo.setSurname("Doe");
        userInfo.setEmail("admin@example.com");

        orderItemDto = new OrderItemDto();
        orderItemDto.setItem(item);
        orderItemDto.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setUserId(55);
        orderRequest.setStatus(Status.CREATED);
        orderRequest.setItems(List.of(orderItemDto));
        orderRequest.setTotalPrice(200.0);

        orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setQuantity(2);

        order = new Order();
        order.setId(1);
        order.setUserId(1);
        order.setStatus(Status.CREATED);
        order.setTotalPrice(200.0);
        order.setDeleted(false);
        order.setOrderItems(List.of(orderItem));

        orderDto = new OrderDto();
        orderDto.setId(1);
        orderDto.setUserId(1);
        orderDto.setStatus(Status.CREATED);
        orderDto.setItems(List.of(orderItemDto));

        orderResponse = new OrderResponse();
        orderResponse.setName("John");
        orderResponse.setSurname("Doe");
        orderResponse.setEmail("admin@example.com");
        orderResponse.setOrderDto(orderDto);
    }

    @Test
    public void createOrderSuccess() {
        // Arrange:
        when(orderMapper.createOrder(orderRequest)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(userCacheService.getUserInfo(anyString())).thenReturn(userInfo);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(orderResponseMapper.toOrderResponse(userInfo, orderDto)).thenReturn(orderResponse);

        // Act:
        OrderResponse result = orderService.createOrder(orderRequest);

        // Assert:
        assertNotNull(result);
        assertEquals(orderResponse.getName(), result.getName());
        assertEquals(orderResponse.getSurname(), result.getSurname());
        assertEquals(orderResponse.getEmail(), result.getEmail());
        assertEquals(orderResponse.getOrderDto().getId(), result.getOrderDto().getId());
        assertEquals(orderResponse.getOrderDto().getUserId(), result.getOrderDto().getUserId());
        assertEquals(orderResponse.getOrderDto().getStatus(), result.getOrderDto().getStatus());
        assertEquals(orderResponse.getOrderDto().getItems().size(), result.getOrderDto().getItems().size());

        // Verify:
        verify(orderMapper, times(1)).createOrder(orderRequest);
        verify(orderRepository, times(1)).save(order);
        verify(userCacheService, times(1)).getUserInfo(anyString());
        verify(orderMapper, times(1)).toOrderDto(order);
        verify(orderResponseMapper, times(1)).toOrderResponse(userInfo, orderDto);
    }

    @Test
    public void getOrderByIdSuccess() {
        // Arrange:
        when(orderRepository.findOrderById(1)).thenReturn(Optional.of(order));
        when(userCacheService.getUserInfo(anyString())).thenReturn(userInfo);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(orderResponseMapper.toOrderResponse(userInfo, orderDto)).thenReturn(orderResponse);

        // Act:
        OrderResponse result = orderService.getOrderById(1);

        // Assert:
        assertNotNull(result);
        assertEquals(orderResponse.getName(), result.getName());
        assertEquals(orderResponse.getSurname(), result.getSurname());
        assertEquals(orderResponse.getEmail(), result.getEmail());
        assertEquals(orderResponse.getOrderDto().getId(), result.getOrderDto().getId());
        assertEquals(orderResponse.getOrderDto().getUserId(), result.getOrderDto().getUserId());
        assertEquals(orderResponse.getOrderDto().getStatus(), result.getOrderDto().getStatus());
        assertEquals(orderResponse.getOrderDto().getItems().size(), result.getOrderDto().getItems().size());

        // Verify:
        verify(orderRepository, times(1)).findOrderById(1);
        verify(userCacheService, times(1)).getUserInfo(anyString());
        verify(orderMapper, times(1)).toOrderDto(order);
        verify(orderResponseMapper, times(1)).toOrderResponse(userInfo, orderDto);
    }

    @Test
    public void getOrderByIdThrowErrorNotFoundException() {
        // Arrange:
        when(orderRepository.findOrderById(1)).thenReturn(Optional.empty());

        // Act:
        NotFoundException result = assertThrows(NotFoundException.class, () -> orderService.getOrderById(1));

        // Assert:
        assertNotNull(result);
        assertEquals(ErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(1), result.getMessage());

        // Verify:
        verify(orderRepository, times(1)).findOrderById(1);
    }

    @Test
    public void getAllOrdersSuccess() {
        // Arrange:
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(userCacheService.getUserInfo(anyString())).thenReturn(userInfo);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(orderResponseMapper.toOrderResponse(userInfo, orderDto)).thenReturn(orderResponse);

        // Act:
        Page<OrderResponse> result = orderService.getAllOrders(null, null, null, pageable);

        // Assert:
        assertNotNull(result);
        assertEquals(orderResponse.getName(), result.getContent().getFirst().getName());
        assertEquals(orderResponse.getSurname(), result.getContent().getFirst().getSurname());
        assertEquals(orderResponse.getEmail(), result.getContent().getFirst().getEmail());
        assertEquals(orderResponse.getOrderDto().getId(), result.getContent().getFirst().getOrderDto().getId());
        assertEquals(orderResponse.getOrderDto().getUserId(), result.getContent().getFirst().getOrderDto().getUserId());

        // Verify:
        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userCacheService, times(1)).getUserInfo(anyString());
        verify(orderMapper, times(1)).toOrderDto(order);
        verify(orderResponseMapper, times(1)).toOrderResponse(userInfo, orderDto);
    }

    @Test
    public void getAllOrdersByUserIdSuccess() {
        // Arrange:
        when(orderRepository.findAllOrdersByUserId(1)).thenReturn(List.of(order));
        when(userCacheService.getUserInfo(anyString())).thenReturn(userInfo);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(orderResponseMapper.toOrderResponse(userInfo, orderDto)).thenReturn(orderResponse);

        // Act:
        List<OrderResponse> result = orderService.getOrdersByUserId(1);

        // Assert:
        assertNotNull(result);
        assertEquals(orderResponse.getName(), result.getFirst().getName());
        assertEquals(orderResponse.getSurname(), result.getFirst().getSurname());
        assertEquals(orderResponse.getEmail(), result.getFirst().getEmail());
        assertEquals(orderResponse.getOrderDto().getId(), result.getFirst().getOrderDto().getId());
        assertEquals(orderResponse.getOrderDto().getUserId(), result.getFirst().getOrderDto().getUserId());

        // Verify:
        verify(orderRepository, times(1)).findAllOrdersByUserId(1);
        verify(userCacheService, times(1)).getUserInfo(anyString());
        verify(orderMapper, times(1)).toOrderDto(order);
    }

    @Test
    public void updateOrderByIdSuccess() {
        // Arrange:
        orderRequest.setStatus(Status.IN_PROCESS);
        orderResponse.getOrderDto().setStatus(Status.IN_PROCESS);

        when(orderRepository.findOrderById(1)).thenReturn(Optional.of(order));
        doNothing().when(orderMapper).updateOrder(orderRequest, order);
        when(userCacheService.getUserInfo(anyString())).thenReturn(userInfo);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(orderResponseMapper.toOrderResponse(userInfo, orderDto)).thenReturn(orderResponse);

        // Act:
        OrderResponse result = orderService.updateOrderById(1, orderRequest);

        // Assert:
        assertNotNull(result);
        assertEquals(orderResponse.getName(), result.getName());
        assertEquals(orderResponse.getSurname(), result.getSurname());
        assertEquals(orderResponse.getEmail(), result.getEmail());
        assertEquals(orderResponse.getOrderDto().getId(), result.getOrderDto().getId());
        assertEquals(orderResponse.getOrderDto().getUserId(), result.getOrderDto().getUserId());
        assertEquals(Status.IN_PROCESS, result.getOrderDto().getStatus());

        // Verify:
        verify(orderRepository, times(1)).findOrderById(1);
        verify(userCacheService, times(1)).getUserInfo(anyString());
        verify(orderMapper, times(1)).updateOrder(orderRequest, order);
        verify(orderResponseMapper, times(1)).toOrderResponse(userInfo, orderDto);
    }

    @Test
    public void updateOrderByIdThrowErrorNotFoundException() {
        // Arrange:
        when(orderRepository.findOrderById(1)).thenReturn(Optional.empty());

        // Act:
        NotFoundException result = assertThrows(NotFoundException.class, () -> orderService.updateOrderById(1, orderRequest));

        // Assert:
        assertNotNull(result);
        assertEquals(ErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(1), result.getMessage());

        // Verify:
        verify(orderRepository, times(1)).findOrderById(1);
    }

    @Test
    public void deleteOrderSuccess() {
        // Arrange:
        when(orderRepository.findOrderById(1)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).softDelete(1);

        // Act:
        orderService.deleteOrderById(1);

        // Verify:
        verify(orderRepository, times(1)).findOrderById(1);
        verify(orderRepository, times(1)).softDelete(1);
    }

    @Test
    public void deleteOrderThrowErrorNotFoundException() {
        // Arrange
        when(orderRepository.findOrderById(1)).thenReturn(Optional.empty());

        // Act:
        NotFoundException result = assertThrows(NotFoundException.class, () -> orderService.deleteOrderById(1));

        // Assert:
        assertNotNull(result);
        assertEquals(ErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(1), result.getMessage());

        // Verify:
        verify(orderRepository, times(1)).findOrderById(1);
    }
}
