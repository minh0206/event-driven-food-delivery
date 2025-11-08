package com.fooddelivery.restaurantservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.mapper.MenuItemMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantMapper;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.service.RestaurantService;

@ExtendWith(MockitoExtension.class)
class PublicRestaurantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurantService restaurantService;
    @Mock
    private RestaurantMapper restaurantMapper;
    @Mock
    private MenuItemMapper menuItemMapper;

    @InjectMocks
    private PublicRestaurantController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getAllRestaurants_returnsPagedDtos() throws Exception {
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("R1");
        Page<Restaurant> page = new PageImpl<>(List.of(r), PageRequest.of(0, 1), 1);
        when(restaurantService.getAllRestaurants(any(Pageable.class))).thenReturn(page);
        when(restaurantMapper.toDto(any(Restaurant.class))).thenReturn(new RestaurantDto(1L, "R1", null, null));

        mockMvc.perform(get("/api/restaurants?page=0&size=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].restaurantName").value("R1"));
    }

    @Test
    void getRestaurantById_returnsDto() throws Exception {
        Restaurant r = new Restaurant();
        r.setId(2L);
        when(restaurantService.getRestaurantById(2L)).thenReturn(r);
        when(restaurantMapper.toDto(r)).thenReturn(new RestaurantDto(2L, "R2", "A", "C"));

        mockMvc.perform(get("/api/restaurants/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.restaurantName").value("R2"));
    }

    @Test
    void getRestaurantMenu_returnsList() throws Exception {
        MenuItem m = new MenuItem();
        m.setId(11L);
        m.setName("N");
        m.setPrice(new BigDecimal("9.99"));
        when(restaurantService.getRestaurantMenu(5L)).thenReturn(List.of(m));
        when(menuItemMapper.toDto(m)).thenReturn(new MenuItemDto(11L, "N", null, new BigDecimal("9.99")));

        mockMvc.perform(get("/api/restaurants/5/menu").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].name").value("N"));
    }
}
