package com.fooddelivery.orderservice.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderItem;
import com.fooddelivery.orderservice.service.OrderService;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.shared.dto.DriverOrderDto;
import com.fooddelivery.shared.dto.OrderItemDto;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.exception.GlobalExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(InternalOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class InternalOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    private JwtService jwtService;

    private Order testOrder;
    private DriverOrderDto testDriverOrderDto;

    @BeforeEach
    void setup() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(100L);
        testOrder.setRestaurantId(200L);
        testOrder.setDriverUserId(300L);
        testOrder.setStatus(OrderStatus.ACCEPTED);
        testOrder.setTotalPrice(new BigDecimal("50.00"));
        testOrder.setCreatedAt(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setMenuItemId(10L);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("25.00"));
        testOrder.setItems(List.of(orderItem));

        OrderItemDto itemDto = new OrderItemDto(10L, 2, new BigDecimal("25.00"));
        testDriverOrderDto = new DriverOrderDto(
                1L,
                100L,
                200L,
                OrderStatus.ACCEPTED,
                new BigDecimal("50.00"),
                List.of(itemDto),
                LocalDateTime.now());
    }

    @Test
    void getDriverOrder_withValidOrderId_returnsDriverOrderDto() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(orderMapper.toDriverOrderDto(testOrder)).thenReturn(testDriverOrderDto);

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(100))
                .andExpect(jsonPath("$.restaurantId").value(200))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.totalPrice").value(50.00));

        verify(orderService, times(1)).getOrderById(1L);
        verify(orderMapper, times(1)).toDriverOrderDto(testOrder);
    }

    @Test
    void getDriverOrder_withInvalidOrderId_returnsNotFound() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L))
                .thenThrow(new EntityNotFoundException("Order not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/internal/orders/driver/999"));

        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    void getDriverOrder_withPendingOrder_returnsOrderWithNullDriver() throws Exception {
        // Arrange
        Order pendingOrder = new Order();
        pendingOrder.setId(2L);
        pendingOrder.setCustomerId(101L);
        pendingOrder.setRestaurantId(201L);
        pendingOrder.setDriverUserId(null);
        pendingOrder.setStatus(OrderStatus.PENDING);
        pendingOrder.setTotalPrice(new BigDecimal("30.00"));

        DriverOrderDto pendingDto = new DriverOrderDto(
                2L,
                101L,
                201L,
                OrderStatus.PENDING,
                new BigDecimal("30.00"),
                List.of(),
                LocalDateTime.now());

        when(orderService.getOrderById(2L)).thenReturn(pendingOrder);
        when(orderMapper.toDriverOrderDto(pendingOrder)).thenReturn(pendingDto);

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).getOrderById(2L);
    }

    @Test
    void getDriverOrder_withDeliveredOrder_returnsCompletedOrder() throws Exception {
        // Arrange
        Order deliveredOrder = new Order();
        deliveredOrder.setId(3L);
        deliveredOrder.setCustomerId(102L);
        deliveredOrder.setRestaurantId(202L);
        deliveredOrder.setDriverUserId(301L);
        deliveredOrder.setStatus(OrderStatus.DELIVERED);
        deliveredOrder.setTotalPrice(new BigDecimal("75.00"));

        DriverOrderDto deliveredDto = new DriverOrderDto(
                3L,
                102L,
                202L,
                OrderStatus.DELIVERED,
                new BigDecimal("75.00"),
                List.of(),
                LocalDateTime.now());

        when(orderService.getOrderById(3L)).thenReturn(deliveredOrder);
        when(orderMapper.toDriverOrderDto(deliveredOrder)).thenReturn(deliveredDto);

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("DELIVERED"));

        verify(orderService, times(1)).getOrderById(3L);
    }

    @Test
    void getDriverOrder_withMultipleItems_returnsAllItems() throws Exception {
        // Arrange
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setMenuItemId(10L);
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("15.00"));

        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setMenuItemId(11L);
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("20.00"));

        Order orderWithMultipleItems = new Order();
        orderWithMultipleItems.setId(4L);
        orderWithMultipleItems.setCustomerId(103L);
        orderWithMultipleItems.setRestaurantId(203L);
        orderWithMultipleItems.setDriverUserId(302L);
        orderWithMultipleItems.setStatus(OrderStatus.ACCEPTED);
        orderWithMultipleItems.setTotalPrice(new BigDecimal("50.00"));
        orderWithMultipleItems.setItems(List.of(item1, item2));

        OrderItemDto itemDto1 = new OrderItemDto(10L, 2, new BigDecimal("15.00"));
        OrderItemDto itemDto2 = new OrderItemDto(11L, 1, new BigDecimal("20.00"));

        DriverOrderDto dtoWithMultipleItems = new DriverOrderDto(
                4L,
                103L,
                203L,
                OrderStatus.ACCEPTED,
                new BigDecimal("50.00"),
                List.of(itemDto1, itemDto2),
                LocalDateTime.now());

        when(orderService.getOrderById(4L)).thenReturn(orderWithMultipleItems);
        when(orderMapper.toDriverOrderDto(orderWithMultipleItems)).thenReturn(dtoWithMultipleItems);

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2));

        verify(orderService, times(1)).getOrderById(4L);
    }

    @Test
    void getDriverOrder_verifysMappingIsInvoked() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(orderMapper.toDriverOrderDto(testOrder)).thenReturn(testDriverOrderDto);

        // Act
        mockMvc.perform(get("/internal/orders/driver/1"))
                .andExpect(status().isOk());

        // Assert - Verify that the mapper was called with the correct order
        verify(orderMapper, times(1)).toDriverOrderDto(testOrder);
    }

    @Test
    void getDriverOrder_withZeroOrderId_handlesGracefully() throws Exception {
        // Arrange
        when(orderService.getOrderById(0L))
                .thenThrow(new EntityNotFoundException("Order not found with id: 0"));

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found with id: 0"))
                .andExpect(jsonPath("$.path").value("/internal/orders/driver/0"));

        verify(orderService, times(1)).getOrderById(0L);
    }

    @Test
    void getDriverOrder_withNegativeOrderId_handlesGracefully() throws Exception {
        // Arrange
        when(orderService.getOrderById(-1L))
                .thenThrow(new EntityNotFoundException("Order not found with id: -1"));

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found with id: -1"))
                .andExpect(jsonPath("$.path").value("/internal/orders/driver/-1"));

        verify(orderService, times(1)).getOrderById(-1L);
    }

    @Test
    void getDriverOrder_withLargeOrderId_processesSuccessfully() throws Exception {
        // Arrange
        Long largeOrderId = 999999999L;
        Order largeIdOrder = new Order();
        largeIdOrder.setId(largeOrderId);
        largeIdOrder.setCustomerId(100L);
        largeIdOrder.setRestaurantId(200L);
        largeIdOrder.setStatus(OrderStatus.ACCEPTED);
        largeIdOrder.setTotalPrice(new BigDecimal("100.00"));

        DriverOrderDto largeIdDto = new DriverOrderDto(
                largeOrderId,
                100L,
                200L,
                OrderStatus.ACCEPTED,
                new BigDecimal("100.00"),
                List.of(),
                LocalDateTime.now());

        when(orderService.getOrderById(largeOrderId)).thenReturn(largeIdOrder);
        when(orderMapper.toDriverOrderDto(largeIdOrder)).thenReturn(largeIdDto);

        // Act & Assert
        mockMvc.perform(get("/internal/orders/driver/" + largeOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(largeOrderId));

        verify(orderService, times(1)).getOrderById(largeOrderId);
    }
}
