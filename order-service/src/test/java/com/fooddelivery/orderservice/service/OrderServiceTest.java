package com.fooddelivery.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fooddelivery.orderservice.dto.CreateOrderRequestDto;
import com.fooddelivery.orderservice.dto.OrderItemRequestDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderItem;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.OrderPlacedEvent;
import com.fooddelivery.shared.publisher.OrderPlacedEventPublisher;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPlacedEventPublisher orderPlacedEventPublisher;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setup() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(100L);
        testOrder.setRestaurantId(200L);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(new BigDecimal("50.00"));
        testOrder.setCreatedAt(LocalDateTime.now());

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setMenuItemId(10L);
        testOrderItem.setQuantity(2);
        testOrderItem.setPrice(new BigDecimal("25.00"));
        testOrderItem.setOrder(testOrder);

        testOrder.setItems(List.of(testOrderItem));
    }

    @Test
    void getOrderById_withValidId_returnsOrder() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getCustomerId());
        assertEquals(200L, result.getRestaurantId());
        verify(orderRepository, times(1)).findByIdWithItems(1L);
    }

    @Test
    void getOrderById_withInvalidId_throwsEntityNotFoundException() {
        // Arrange
        when(orderRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.getOrderById(999L));

        assertEquals("Order not found with id: 999", exception.getMessage());
        verify(orderRepository, times(1)).findByIdWithItems(999L);
    }

    @Test
    void getOrdersByCustomerId_returnsOrdersList() {
        // Arrange
        List<Order> expectedOrders = List.of(testOrder);
        when(orderRepository.findByCustomerIdOrderByCreatedAtDesc(100L)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.getOrdersByCustomerId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getCustomerId());
        verify(orderRepository, times(1)).findByCustomerIdOrderByCreatedAtDesc(100L);
    }

    @Test
    void getOrdersByCustomerId_withNoOrders_returnsEmptyList() {
        // Arrange
        when(orderRepository.findByCustomerIdOrderByCreatedAtDesc(999L)).thenReturn(List.of());

        // Act
        List<Order> result = orderService.getOrdersByCustomerId(999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void createOrder_savesOrderAndPublishesEvent() {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 2, new BigDecimal("25.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(requestDto, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getCustomerId());
        assertEquals(200L, result.getRestaurantId());
        assertEquals(OrderStatus.PENDING, result.getStatus());

        // Verify save was called
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(100L, savedOrder.getCustomerId());
        assertEquals(200L, savedOrder.getRestaurantId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());

        // Verify event was published
        verify(orderPlacedEventPublisher, times(1)).publish(any(OrderPlacedEvent.class));
    }

    @Test
    void createOrder_withMultipleItems_calculatesCorrectTotalPrice() {
        // Arrange
        OrderItemRequestDto item1 = new OrderItemRequestDto(10L, 2, new BigDecimal("15.00"));
        OrderItemRequestDto item2 = new OrderItemRequestDto(11L, 3, new BigDecimal("10.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(item1, item2));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCustomerId(100L);
        savedOrder.setRestaurantId(200L);
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setTotalPrice(new BigDecimal("60.00"));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(requestDto, 100L);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(new BigDecimal("60.00"), capturedOrder.getTotalPrice()); // (15 * 2) + (10 * 3)
        assertEquals(2, capturedOrder.getItems().size());
    }

    @Test
    void createOrder_setsCorrectOrderStatus() {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 1, new BigDecimal("20.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.createOrder(requestDto, 100L);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(OrderStatus.PENDING, capturedOrder.getStatus());
    }

    @Test
    void createOrder_setsCreatedAtTimestamp() {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 1, new BigDecimal("20.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.createOrder(requestDto, 100L);

        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertNotNull(capturedOrder.getCreatedAt());
        assertEquals(true, capturedOrder.getCreatedAt().isAfter(beforeCreation));
        assertEquals(true, capturedOrder.getCreatedAt().isBefore(afterCreation));
    }

    @Test
    void createOrder_linksOrderItemsToOrder() {
        // Arrange
        OrderItemRequestDto item1 = new OrderItemRequestDto(10L, 2, new BigDecimal("15.00"));
        OrderItemRequestDto item2 = new OrderItemRequestDto(11L, 1, new BigDecimal("20.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(item1, item2));

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.createOrder(requestDto, 100L);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(2, capturedOrder.getItems().size());

        // Verify each item is linked to the order
        for (OrderItem item : capturedOrder.getItems()) {
            assertEquals(capturedOrder, item.getOrder());
        }
    }

    @Test
    void createOrder_publishesEventWithCorrectDetails() {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 2, new BigDecimal("25.00"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.createOrder(requestDto, 100L);

        // Assert
        ArgumentCaptor<OrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(orderPlacedEventPublisher).publish(eventCaptor.capture());

        OrderPlacedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(testOrder.getId(), capturedEvent.orderId());
        assertEquals(testOrder.getRestaurantId(), capturedEvent.restaurantId());
        assertNotNull(capturedEvent.items());
    }

    @Test
    void createOrder_withSingleItem_calculatesCorrectTotal() {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(10L, 5, new BigDecimal("12.50"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(itemDto));

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.createOrder(requestDto, 100L);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(new BigDecimal("62.50"), capturedOrder.getTotalPrice()); // 12.50 * 5
    }

    @Test
    void createOrder_withDecimalQuantities_handlesCorrectly() {
        // Arrange
        OrderItemRequestDto item1 = new OrderItemRequestDto(10L, 3, new BigDecimal("9.99"));
        OrderItemRequestDto item2 = new OrderItemRequestDto(11L, 2, new BigDecimal("15.49"));
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(200L, List.of(item1, item2));

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.createOrder(requestDto, 100L);

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(new BigDecimal("60.95"), capturedOrder.getTotalPrice()); // (9.99 * 3) + (15.49 * 2)
    }
}
