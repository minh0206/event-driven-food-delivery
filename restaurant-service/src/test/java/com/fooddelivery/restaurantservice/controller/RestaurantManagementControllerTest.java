package com.fooddelivery.restaurantservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderRequestDto;
import com.fooddelivery.restaurantservice.mapper.MenuItemMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantOrderMapper;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.restaurantservice.service.RestaurantService;
import com.fooddelivery.shared.dto.RestaurantRequestDto;
import com.fooddelivery.shared.enumerate.OrderStatus;

@ExtendWith(MockitoExtension.class)
class RestaurantManagementControllerTest {

    private final Principal principal = () -> "50"; // ownerId = 50
    private MockMvc mockMvc;
    @Mock
    private RestaurantMapper restaurantMapper;
    @Mock
    private MenuItemMapper menuItemMapper;
    @Mock
    private RestaurantService restaurantService;
    @Mock
    private RestaurantOrderMapper restaurantOrderMapper;
    @InjectMocks
    private RestaurantManagementController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void updateRestaurant_returnsDto() throws Exception {
        Restaurant updated = new Restaurant();
        updated.setId(1L);
        when(restaurantService.updateRestaurant(eq(1L), any(RestaurantRequestDto.class), eq(50L))).thenReturn(updated);
        when(restaurantMapper.toDto(updated)).thenReturn(new RestaurantDto(1L, "New", "Addr", "Cuisine"));

        String body = "{\"restaurantName\":\"New\",\"address\":\"Addr\",\"cuisineType\":\"Cuisine\"}";
        mockMvc.perform(put("/api/restaurants/manage/1").principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.restaurantName").value("New"));
    }

    @Test
    void addMenuItem_returns201() throws Exception {
        MenuItem item = new MenuItem();
        item.setId(10L);
        item.setName("Burger");
        item.setPrice(new BigDecimal("12.50"));
        when(restaurantService.addMenuItem(eq(1L), any(), eq(50L))).thenReturn(item);
        when(menuItemMapper.toDto(item)).thenReturn(new MenuItemDto(10L, "Burger", null, new BigDecimal("12.50")));

        String body = "{\"name\":\"Burger\",\"description\":\"Yum\",\"price\":12.50}";
        mockMvc.perform(post("/api/restaurants/manage/1/menu").principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Burger"));
    }

    @Test
    void updateMenuItem_returnsDto() throws Exception {
        MenuItem item = new MenuItem();
        item.setId(11L);
        item.setName("Fries");
        item.setPrice(new BigDecimal("4.00"));
        when(restaurantService.updateMenuItem(eq(1L), eq(11L), any(), eq(50L))).thenReturn(item);
        when(menuItemMapper.toDto(item)).thenReturn(new MenuItemDto(11L, "Fries", null, new BigDecimal("4.00")));

        String body = "{\"name\":\"Fries\",\"description\":\"Crispy\",\"price\":4.00}";
        mockMvc.perform(put("/api/restaurants/manage/1/menu/11").principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.name").value("Fries"));
    }

    @Test
    void deleteMenuItem_returns204() throws Exception {
        doNothing().when(restaurantService).deleteMenuItem(1L, 11L, 50L);
        mockMvc.perform(delete("/api/restaurants/manage/1/menu/11").principal(principal))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateRestaurantOrder_returnsDtoAndPublishes() throws Exception {
        RestaurantOrder saved = new RestaurantOrder();
        saved.setOrderId(201L);
        saved.setRestaurantId(1L);
        saved.setLocalStatus(OrderStatus.ACCEPTED);
        when(restaurantService.updateRestaurantOrder(eq(201L), any(RestaurantOrderRequestDto.class), eq(50L)))
                .thenReturn(saved);
        when(restaurantOrderMapper.toDto(saved))
                .thenReturn(new RestaurantOrderDto(201L, OrderStatus.ACCEPTED, null, List.of()));

        String body = "{\"status\":\"ACCEPTED\",\"assignedCook\":\"John\",\"internalNotes\":\"ok\"}";
        mockMvc.perform(put("/api/restaurants/manage/orders/201").principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(201))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
}
