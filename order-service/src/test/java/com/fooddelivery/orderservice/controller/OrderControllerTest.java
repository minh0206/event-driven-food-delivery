package com.fooddelivery.orderservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.orderservice.dto.CreateOrderRequestDto;
import com.fooddelivery.orderservice.dto.CustomerOrderDto;
import com.fooddelivery.orderservice.dto.OrderItemRequestDto;
import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderItem;
import com.fooddelivery.orderservice.service.OrderService;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.shared.dto.OrderItemDto;
import com.fooddelivery.shared.enumerate.OrderStatus;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    private JwtService jwtService;

    private Order testOrder;
    private CustomerOrderDto testOrderDto;

    @BeforeEach
    void setup() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(100L);
        testOrder.setRestaurantId(200L);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(new BigDecimal("50.00"));
        testOrder.setCreatedAt(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setMenuItemId(10L);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("25.00"));
        testOrder.setItems(List.of(orderItem));

        OrderItemDto itemDto = new OrderItemDto(10L, 2, new BigDecimal("25.00"));
        testOrderDto = new CustomerOrderDto(
                1L,
                100L,
                200L,
                null,
                OrderStatus.PENDING,
                new BigDecimal("50.00"),
                List.of(itemDto),
                LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "100", roles = "CUSTOMER")
    void createOrder_withValidRequest_returnsCreatedOrder() throws Exception {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 2, new BigDecimal("25.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        when(orderService.createOrder(any(CreateOrderRequestDto.class), eq(100L))).thenReturn(testOrder);
        when(orderMapper.toCustomerOrderDto(testOrder)).thenReturn(testOrderDto);

        // Act & Assert
        mockMvc.perform(post("/api/orders").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(100))
                .andExpect(jsonPath("$.restaurantId").value(200))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalPrice").value(50.00));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequestDto.class), eq(100L));
        verify(orderMapper, times(1)).toCustomerOrderDto(testOrder);
    }

    @Test
    @WithMockUser(username = "100", roles = "CUSTOMER")
    void createOrder_withMultipleItems_processesSuccessfully() throws Exception {
        // Arrange
        OrderItemRequestDto item1 = new OrderItemRequestDto(10L, 2, new BigDecimal("15.00"));
        OrderItemRequestDto item2 = new OrderItemRequestDto(11L, 1, new BigDecimal("20.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(item1, item2));

        when(orderService.createOrder(any(CreateOrderRequestDto.class), eq(100L))).thenReturn(testOrder);
        when(orderMapper.toCustomerOrderDto(testOrder)).thenReturn(testOrderDto);

        // Act & Assert
        mockMvc.perform(post("/api/orders").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(orderService, times(1)).createOrder(any(CreateOrderRequestDto.class), eq(100L));
    }

    @Test
    @WithMockUser(username = "100", roles = "CUSTOMER")
    void getOrdersByUserId_returnsUserOrders() throws Exception {
        // Arrange
        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomerId(100L);
        order2.setRestaurantId(201L);
        order2.setStatus(OrderStatus.ACCEPTED);
        order2.setTotalPrice(new BigDecimal("75.00"));

        CustomerOrderDto orderDto2 = new CustomerOrderDto(
                2L,
                100L,
                201L,
                null,
                OrderStatus.ACCEPTED,
                new BigDecimal("75.00"),
                List.of(),
                LocalDateTime.now());

        when(orderService.getOrdersByCustomerId(100L)).thenReturn(List.of(testOrder, order2));
        when(orderMapper.toCustomerOrderDto(testOrder)).thenReturn(testOrderDto);
        when(orderMapper.toCustomerOrderDto(order2)).thenReturn(orderDto2);

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(orderService, times(1)).getOrdersByCustomerId(100L);
        verify(orderMapper, times(2)).toCustomerOrderDto(any(Order.class));
    }

    @Test
    @WithMockUser(username = "100", roles = "CUSTOMER")
    void getOrdersByUserId_withNoOrders_returnsEmptyList() throws Exception {
        // Arrange
        when(orderService.getOrdersByCustomerId(100L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderService, times(1)).getOrdersByCustomerId(100L);
    }

    @Test
    @WithMockUser(username = "100", roles = "CUSTOMER")
    void createOrder_extractsCustomerIdFromPrincipal() throws Exception {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 1, new BigDecimal("20.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        when(orderService.createOrder(any(CreateOrderRequestDto.class), eq(100L))).thenReturn(testOrder);
        when(orderMapper.toCustomerOrderDto(testOrder)).thenReturn(testOrderDto);

        // Act & Assert
        mockMvc.perform(post("/api/orders").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // Verify that the customer ID from the principal (100) was used
        verify(orderService, times(1)).createOrder(any(CreateOrderRequestDto.class), eq(100L));
    }

    @Test
    @WithMockUser(username = "100", roles = "CUSTOMER")
    void createOrder_withLargeQuantity_processesSuccessfully() throws Exception {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 100, new BigDecimal("5.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        when(orderService.createOrder(any(CreateOrderRequestDto.class), eq(100L))).thenReturn(testOrder);
        when(orderMapper.toCustomerOrderDto(testOrder)).thenReturn(testOrderDto);

        // Act & Assert
        mockMvc.perform(post("/api/orders").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(orderService, times(1)).createOrder(any(CreateOrderRequestDto.class), eq(100L));
    }
}
