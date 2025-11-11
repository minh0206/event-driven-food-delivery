package com.fooddelivery.restaurantservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.fooddelivery.restaurantservice.dto.RestaurantOrderDto;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.restaurantservice.model.RestaurantOrderItem;
import com.fooddelivery.shared.enumerate.OrderStatus;

class RestaurantOrderMapperTest {

    private final RestaurantOrderMapper mapper = Mappers.getMapper(RestaurantOrderMapper.class);

    @Test
    void toDto_mapsRestaurantOrderToDto() {
        // Arrange
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(100L);
        order.setRestaurantId(5L);
        order.setLocalStatus(OrderStatus.PENDING);
        LocalDateTime receivedAt = LocalDateTime.now();
        order.setReceivedAt(receivedAt);
        order.setItems(new ArrayList<>());

        // Act
        RestaurantOrderDto dto = mapper.toDto(order);

        // Assert
        assertNotNull(dto);
        assertEquals(100L, dto.orderId());
        assertEquals(OrderStatus.PENDING, dto.status());
        assertEquals(receivedAt, dto.receivedAt());
        assertNotNull(dto.items());
    }

    @Test
    void toDto_withAcceptedStatus_mapsCorrectly() {
        // Arrange
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(200L);
        order.setRestaurantId(10L);
        order.setLocalStatus(OrderStatus.ACCEPTED);
        LocalDateTime receivedAt = LocalDateTime.now().minusMinutes(10);
        order.setReceivedAt(receivedAt);
        order.setItems(new ArrayList<>());

        // Act
        RestaurantOrderDto dto = mapper.toDto(order);

        // Assert
        assertNotNull(dto);
        assertEquals(200L, dto.orderId());
        assertEquals(OrderStatus.ACCEPTED, dto.status());
        assertEquals(receivedAt, dto.receivedAt());
    }

    @Test
    void toDto_withOrderItems_mapsCorrectly() {
        // Arrange
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(300L);
        order.setRestaurantId(15L);
        order.setLocalStatus(OrderStatus.READY_FOR_PICKUP);
        LocalDateTime receivedAt = LocalDateTime.now().minusMinutes(30);
        order.setReceivedAt(receivedAt);

        RestaurantOrderItem item1 = new RestaurantOrderItem();
        item1.setOrderItemId(1L);
        item1.setMenuItemId(10L);
        item1.setQuantity(2);
        item1.setOrder(order);

        RestaurantOrderItem item2 = new RestaurantOrderItem();
        item2.setOrderItemId(2L);
        item2.setMenuItemId(11L);
        item2.setQuantity(1);
        item2.setOrder(order);

        order.setItems(List.of(item1, item2));

        // Act
        RestaurantOrderDto dto = mapper.toDto(order);

        // Assert
        assertNotNull(dto);
        assertEquals(300L, dto.orderId());
        assertEquals(OrderStatus.READY_FOR_PICKUP, dto.status());
        assertEquals(receivedAt, dto.receivedAt());
        assertNotNull(dto.items());
        assertEquals(2, dto.items().size());
    }
}
