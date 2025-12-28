package com.order.service.order_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.order.service.order_service.OrderServiceApplication;

import com.order.service.order_service.model.dto.ItemDto;
import com.order.service.order_service.model.dto.OrderItemDto;
import com.order.service.order_service.model.entity.Item;
import com.order.service.order_service.model.entity.Order;
import com.order.service.order_service.model.entity.OrderItem;
import com.order.service.order_service.model.enums.Status;
import com.order.service.order_service.model.request.OrderRequest;
import com.order.service.order_service.repository.ItemRepository;
import com.order.service.order_service.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;


@ActiveProfiles("test")
@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class OrderControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private OrderItemDto orderItemDto;

    private OrderItem orderItem;
    private Order order;
    private Item item;


    @BeforeEach
    public void setUp() {
        // Очистка репозиториев
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        item = new Item();
        item.setName("test");
        item.setPrice(100.0);
        item = itemRepository.saveAndFlush(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setPrice(item.getPrice());

        order = new Order();
        order.setUserId(1);
        order.setStatus(Status.CREATED);
        order.setTotalPrice(200.0);
        order.setDeleted(false);

        orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(2);
        orderItem.setOrder(order);

        order.setOrderItems(List.of(orderItem));
        order = orderRepository.saveAndFlush(order);

        orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setItem(itemDto);
        orderItemDto.setQuantity(orderItem.getQuantity());

        WireMock.configureFor(WIREMOCK.getHost(), WIREMOCK.getMappedPort(8080));
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/api/user/info/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "name": "John",
                              "surname": "Doe",
                              "birthDate": null,
                              "email": "test@mail.com"
                            }
                            """)));
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    public void getOrderByIdReturn200Ok() throws Exception {
        // Given:
        Integer id = order.getId();

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/order/{id}", id)
                .accept(MediaType.APPLICATION_JSON));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.status").value(Status.CREATED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.totalPrice").value(200.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.deleted").value(false));
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    public void getOrderByIdReturn404NotFound() throws Exception {
        // Given:
        Integer id = 999;

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/order/{id}", id)
                .accept(MediaType.APPLICATION_JSON));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    public void getAllOrdersReturn200Ok() throws Exception {
        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/order/all")
                .accept(MediaType.APPLICATION_JSON));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    public void getOrderByUserIdReturn200Ok() throws Exception {
        // Given:
        Integer id = order.getUserId();

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/order/user/{id}", id)
                .accept(MediaType.APPLICATION_JSON));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].surname").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("test@mail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderDto.userId").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderDto.status").value(Status.CREATED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderDto.items[0].item.name").value(item.getName()));
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    public void createOrderReturn201Created() throws Exception {
        // Given:
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setPrice(item.getPrice());

        OrderItemDto requestItemDto = new OrderItemDto();
        requestItemDto.setItem(itemDto);
        requestItemDto.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(1);
        orderRequest.setStatus(Status.CREATED);
        orderRequest.setItems(List.of(requestItemDto));
        orderRequest.setTotalPrice(item.getPrice() * orderItemDto.getQuantity());

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/order/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.status").value(Status.CREATED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.items[0].item.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.items[0].item.price").value(itemDto.getPrice()));
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    public void deleteOrderReturn204NoContent() throws Exception {
        // Given:
        Integer id = order.getId();

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/order/delete/{id}", id));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertEquals(Boolean.TRUE, orderRepository.findOrderById(id).get().getDeleted());
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    public void deleteOrderReturn404NotFound() throws Exception {
        // Given:
        Integer id = 999;

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/order/delete/{id}", id));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}