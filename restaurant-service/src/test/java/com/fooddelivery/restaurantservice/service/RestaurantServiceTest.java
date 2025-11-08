package com.fooddelivery.restaurantservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.publisher.OrderAcceptedEventPublisher;
import com.fooddelivery.shared.publisher.OrderReadyEventPublisher;
import com.fooddelivery.shared.publisher.OrderRejectedEventPublisher;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private RestaurantOrderRepository restaurantOrderRepository;
    @Mock
    private OrderAcceptedEventPublisher orderAcceptedEventPublisher;
    @Mock
    private OrderReadyEventPublisher orderReadyEventPublisher;
    @Mock
    private OrderRejectedEventPublisher orderRejectedEventPublisher;

    @InjectMocks
    private RestaurantService restaurantService;

    private RestaurantRequestDto sampleRestaurantRequest() {
        return new RestaurantRequestDto("Resto", "Addr", "Cuisine");
    }

    private Restaurant makeRestaurant(Long id, Long ownerId) {
        Restaurant r = new Restaurant();
        r.setId(id);
        r.setOwnerId(ownerId);
        r.setName("Old");
        r.setAddress("Old");
        r.setCuisineType("Old");
        return r;
    }

    @Test
    void createRestaurant_whenOwnerAlreadyHasOne_throws() {
        when(restaurantRepository.findByOwnerId(10L)).thenReturn(Optional.of(new Restaurant()));
        assertThrows(IllegalStateException.class,
                () -> restaurantService.createRestaurant(sampleRestaurantRequest(), 10L));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void updateRestaurant_unauthorized_throws() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(makeRestaurant(1L, 2L)));
        assertThrows(SecurityException.class,
                () -> restaurantService.updateRestaurant(1L, sampleRestaurantRequest(), 9L));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void updateRestaurant_success_updatesFieldsAndSaves() {
        Restaurant existing = makeRestaurant(1L, 5L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        RestaurantRequestDto req = new RestaurantRequestDto("NewName", "NewAddr", "NewCuisine");
        Restaurant result = restaurantService.updateRestaurant(1L, req, 5L);

        assertEquals("NewName", result.getName());
        assertEquals("NewAddr", result.getAddress());
        assertEquals("NewCuisine", result.getCuisineType());
        verify(restaurantRepository).save(existing);
    }

    @Test
    void addMenuItem_unauthorized_throws() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(makeRestaurant(1L, 7L)));
        assertThrows(SecurityException.class,
                () -> restaurantService.addMenuItem(1L, new MenuItemRequestDto("n", "d", new BigDecimal("10.00")), 8L));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void getRestaurantById_notFound_throws() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> restaurantService.getRestaurantById(99L));
    }

    @Test
    void updateRestaurantOrder_whenAccepted_publishesAcceptedEvent() {
        Restaurant restaurant = makeRestaurant(100L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(200L);
        order.setRestaurantId(100L);
        when(restaurantOrderRepository.findById(200L)).thenReturn(Optional.of(order));
        when(restaurantOrderRepository.save(any(RestaurantOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        RestaurantOrderRequestDto req = new RestaurantOrderRequestDto(OrderStatus.ACCEPTED, "cook", "notes");
        RestaurantOrder updated = restaurantService.updateRestaurantOrder(200L, req, 50L);

        assertEquals(OrderStatus.ACCEPTED, updated.getLocalStatus());
        verify(orderAcceptedEventPublisher).publish(any());
        verify(orderReadyEventPublisher, never()).publish(any());
        verify(orderRejectedEventPublisher, never()).publish(any());
    }

    @Test
    void updateRestaurantOrder_invalidStatus_throws() {
        Restaurant restaurant = makeRestaurant(100L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(200L);
        order.setRestaurantId(100L);
        when(restaurantOrderRepository.findById(200L)).thenReturn(Optional.of(order));

        RestaurantOrderRequestDto req = new RestaurantOrderRequestDto(OrderStatus.PENDING, null, null);
        assertThrows(IllegalArgumentException.class, () -> restaurantService.updateRestaurantOrder(200L, req, 50L));
        verifyNoInteractions(orderAcceptedEventPublisher, orderReadyEventPublisher, orderRejectedEventPublisher);
    }

    @Test
    void updateRestaurantOrder_unauthorized_throws() {
        Restaurant restaurant = makeRestaurant(101L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(200L);
        order.setRestaurantId(100L);
        when(restaurantOrderRepository.findById(200L)).thenReturn(Optional.of(order));

        RestaurantOrderRequestDto req = new RestaurantOrderRequestDto(OrderStatus.ACCEPTED, null, null);
        assertThrows(SecurityException.class, () -> restaurantService.updateRestaurantOrder(200L, req, 50L));
        verify(restaurantOrderRepository, never()).save(any());
    }

    @Test
    void updateRestaurantOrder_nullOrderId_throws() {
        assertThrows(IllegalArgumentException.class, () -> restaurantService.updateRestaurantOrder(null,
                new RestaurantOrderRequestDto(OrderStatus.ACCEPTED, null, null), 1L));
    }
}
