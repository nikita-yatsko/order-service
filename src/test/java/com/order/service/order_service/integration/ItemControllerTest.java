package com.order.service.order_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.order_service.OrderServiceApplication;
import com.order.service.order_service.model.entity.Item;
import com.order.service.order_service.model.request.ItemRequest;
import com.order.service.order_service.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ItemControllerTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Item item;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();

        item = new Item();
        item.setName("test");
        item.setPrice(100.0);

        item = itemRepository.saveAndFlush(item);
    }

    @Test
    public void getAllItemsReturn200Ok() throws Exception {
        // Given:
        Item savedItem = item;

        //When:
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/item/all")
                .accept(MediaType.APPLICATION_JSON)
        );

        // Then:
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(savedItem.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(savedItem.getPrice()));
    }

    @Test
    public void getItemByIdReturn20Ook() throws Exception {
        // Given
        Integer id = item.getId();

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/item/{id}", id)
                .accept(MediaType.APPLICATION_JSON));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(item.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(item.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(item.getPrice()));

    }

    @Test
    public void getItemByIdReturn404NotFound() throws Exception {
        // Given:
        Integer id = 999;

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/item/{id}", id)
                .accept(MediaType.APPLICATION_JSON));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addItemReturn201Created() throws Exception {
        // Given:
        Item newItem = new Item();
        newItem.setName("New item");
        newItem.setPrice(200.0);

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/item/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newItem.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(newItem.getPrice()));
    }

    @Test
    public void addItemReturn409Conflict() throws Exception {
        // Given:
        Item newItem = new Item();
        newItem.setName("test");
        newItem.setPrice(200.0);

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/item/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void updateItemReturn200Ok() throws Exception {
        // Given:
        Integer id = item.getId();
        ItemRequest request = new ItemRequest();
        request.setName("New name");
        request.setPrice(200.0);

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/item/update/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(request.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(request.getPrice()));
    }

    @Test
    public void updateItemReturn404NotFound() throws Exception {
        // Given:
        Integer id = 999;
        ItemRequest request = new ItemRequest();
        request.setName("New name");
        request.setPrice(200.0);

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/item/update/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateItemReturn409DataExist() throws Exception {
        // Given:
        Integer id = item.getId();
        ItemRequest request = new ItemRequest();
        request.setName("New name");
        request.setPrice(200.0);

        Item savedItem = new Item();
        savedItem.setName("New name");
        savedItem.setPrice(200.0);
        itemRepository.save(savedItem);

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/item/update/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void deleteItemReturn204NoContent() throws Exception {
        // Given:
        Integer id = item.getId();

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/item/delete/{id}", id));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteItemReturn404NotFound() throws Exception {
        // Given:
        Integer id = 999;

        // When:
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/item/delete/{id}", id));

        // Then:
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
