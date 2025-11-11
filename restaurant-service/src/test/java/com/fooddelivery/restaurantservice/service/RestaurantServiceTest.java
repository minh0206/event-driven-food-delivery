package com.fooddelivery.restaurantservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderRequestDto;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import com.fooddelivery.shared.dto.RestaurantRequestDto;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.OrderItemDetails;
import com.fooddelivery.shared.event.OrderPlacedEvent;
import com.fooddelivery.shared.publisher.OrderAcceptedEventPublisher;
import com.fooddelivery.shared.publisher.OrderReadyEventPublisher;
import com.fooddelivery.shared.publisher.OrderRejectedEventPublisher;

import jakarta.persistence.EntityExistsException;
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
        assertThrows(EntityExistsException.class,
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
        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurantOrder(null,
                new RestaurantOrderRequestDto(OrderStatus.ACCEPTED, null, null), 1L));
    }

    @Test
    void createRestaurant_success_savesAndReturns() {
        when(restaurantRepository.findByOwnerId(10L)).thenReturn(Optional.empty());
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> {
            Restaurant r = inv.getArgument(0);
            r.setId(100L);
            return r;
        });

        RestaurantRequestDto req = new RestaurantRequestDto("NewResto", "123 Main St", "Italian");
        Restaurant result = restaurantService.createRestaurant(req, 10L);

        assertNotNull(result);
        assertEquals("NewResto", result.getName());
        assertEquals("123 Main St", result.getAddress());
        assertEquals("Italian", result.getCuisineType());
        assertEquals(10L, result.getOwnerId());
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void addMenuItem_success_savesAndReturns() {
        Restaurant restaurant = makeRestaurant(1L, 5L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(inv -> {
            MenuItem item = inv.getArgument(0);
            item.setId(20L);
            return item;
        });

        MenuItemRequestDto req = new MenuItemRequestDto("Pizza", "Delicious", new BigDecimal("15.99"));
        MenuItem result = restaurantService.addMenuItem(1L, req, 5L);

        assertNotNull(result);
        assertEquals("Pizza", result.getName());
        assertEquals("Delicious", result.getDescription());
        assertEquals(new BigDecimal("15.99"), result.getPrice());
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void updateMenuItem_success_updatesAndSaves() {
        Restaurant restaurant = makeRestaurant(1L, 5L);
        MenuItem existingItem = new MenuItem();
        existingItem.setId(10L);
        existingItem.setName("OldName");
        existingItem.setDescription("OldDesc");
        existingItem.setPrice(new BigDecimal("10.00"));

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(existingItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuItemRequestDto req = new MenuItemRequestDto("NewName", "NewDesc", new BigDecimal("12.50"));
        MenuItem result = restaurantService.updateMenuItem(1L, 10L, req, 5L);

        assertEquals("NewName", result.getName());
        assertEquals("NewDesc", result.getDescription());
        assertEquals(new BigDecimal("12.50"), result.getPrice());
        verify(menuItemRepository).save(existingItem);
    }

    @Test
    void updateMenuItem_unauthorized_throws() {
        Restaurant restaurant = makeRestaurant(1L, 5L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        MenuItemRequestDto req = new MenuItemRequestDto("Name", "Desc", new BigDecimal("10.00"));
        assertThrows(SecurityException.class, () -> restaurantService.updateMenuItem(1L, 10L, req, 99L));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void updateMenuItem_restaurantNotFound_throws() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        MenuItemRequestDto req = new MenuItemRequestDto("Name", "Desc", new BigDecimal("10.00"));
        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateMenuItem(999L, 10L, req, 5L));
    }

    @Test
    void updateMenuItem_menuItemNotFound_throws() {
        Restaurant restaurant = makeRestaurant(1L, 5L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(999L)).thenReturn(Optional.empty());

        MenuItemRequestDto req = new MenuItemRequestDto("Name", "Desc", new BigDecimal("10.00"));
        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateMenuItem(1L, 999L, req, 5L));
    }

    @Test
    void deleteMenuItem_success_deletesItem() {
        Restaurant restaurant = makeRestaurant(1L, 5L);
        MenuItem menuItem = new MenuItem();
        menuItem.setId(10L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(menuItem));
        doNothing().when(menuItemRepository).delete(menuItem);

        restaurantService.deleteMenuItem(1L, 10L, 5L);

        verify(menuItemRepository).delete(menuItem);
    }

    @Test
    void deleteMenuItem_unauthorized_throws() {
        Restaurant restaurant = makeRestaurant(1L, 5L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        assertThrows(SecurityException.class, () -> restaurantService.deleteMenuItem(1L, 10L, 99L));
        verify(menuItemRepository, never()).delete(any());
    }

    @Test
    void deleteMenuItem_restaurantNotFound_throws() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.deleteMenuItem(999L, 10L, 5L));
    }

    @Test
    void deleteMenuItem_menuItemNotFound_throws() {
        Restaurant restaurant = makeRestaurant(1L, 5L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.deleteMenuItem(1L, 999L, 5L));
    }

    @Test
    void getRestaurantByOwnerId_success_returnsRestaurant() {
        Restaurant restaurant = makeRestaurant(1L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getRestaurantByOwnerId(50L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(50L, result.getOwnerId());
    }

    @Test
    void getRestaurantByOwnerId_notFound_throws() {
        when(restaurantRepository.findByOwnerId(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.getRestaurantByOwnerId(999L));
    }

    @Test
    void getRestaurantMenu_success_returnsMenuItems() {
        Restaurant restaurant = makeRestaurant(1L, 50L);
        MenuItem item1 = new MenuItem();
        item1.setId(1L);
        item1.setName("Item1");
        MenuItem item2 = new MenuItem();
        item2.setId(2L);
        item2.setName("Item2");
        restaurant.setMenu(List.of(item1, item2));

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        List<MenuItem> result = restaurantService.getRestaurantMenu(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getRestaurantMenu_restaurantNotFound_throws() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.getRestaurantMenu(999L));
    }

    @Test
    void getRestaurantOrders_success_returnsOrders() {
        Restaurant restaurant = makeRestaurant(1L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));

        RestaurantOrder order1 = new RestaurantOrder();
        order1.setOrderId(100L);
        RestaurantOrder order2 = new RestaurantOrder();
        order2.setOrderId(101L);

        when(restaurantOrderRepository.findByRestaurantIdOrderByReceivedAtDesc(1L))
                .thenReturn(List.of(order1, order2));

        List<RestaurantOrder> result = restaurantService.getRestaurantOrders(50L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getRestaurantOrders_restaurantNotFound_throws() {
        when(restaurantRepository.findByOwnerId(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.getRestaurantOrders(999L));
    }

    @Test
    void createRestaurantOrder_success_savesOrder() {
        OrderItemDetails orderItemDetails = new OrderItemDetails(1L, 10L, 2);
        OrderPlacedEvent event = new OrderPlacedEvent(100L, 1L, List.of(orderItemDetails));

        when(restaurantOrderRepository.save(any(RestaurantOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        restaurantService.createRestaurantOrder(event);

        verify(restaurantOrderRepository).save(any(RestaurantOrder.class));
    }

    @Test
    void updateRestaurantOrder_whenReadyForPickup_publishesReadyEvent() {
        Restaurant restaurant = makeRestaurant(100L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(200L);
        order.setRestaurantId(100L);
        when(restaurantOrderRepository.findById(200L)).thenReturn(Optional.of(order));
        when(restaurantOrderRepository.save(any(RestaurantOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        RestaurantOrderRequestDto req = new RestaurantOrderRequestDto(OrderStatus.READY_FOR_PICKUP, null, null);
        RestaurantOrder updated = restaurantService.updateRestaurantOrder(200L, req, 50L);

        assertEquals(OrderStatus.READY_FOR_PICKUP, updated.getLocalStatus());
        verify(orderReadyEventPublisher).publish(any());
        verify(orderAcceptedEventPublisher, never()).publish(any());
        verify(orderRejectedEventPublisher, never()).publish(any());
    }

    @Test
    void updateRestaurantOrder_whenRejected_publishesRejectedEvent() {
        Restaurant restaurant = makeRestaurant(100L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(200L);
        order.setRestaurantId(100L);
        when(restaurantOrderRepository.findById(200L)).thenReturn(Optional.of(order));
        when(restaurantOrderRepository.save(any(RestaurantOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        RestaurantOrderRequestDto req = new RestaurantOrderRequestDto(OrderStatus.REJECTED, null, "Out of stock");
        RestaurantOrder updated = restaurantService.updateRestaurantOrder(200L, req, 50L);

        assertEquals(OrderStatus.REJECTED, updated.getLocalStatus());
        assertEquals("Out of stock", updated.getInternalNotes());
        verify(orderRejectedEventPublisher).publish(any());
        verify(orderAcceptedEventPublisher, never()).publish(any());
        verify(orderReadyEventPublisher, never()).publish(any());
    }

    @Test
    void updateRestaurantOrder_orderNotFound_throws() {
        Restaurant restaurant = makeRestaurant(100L, 50L);
        when(restaurantRepository.findByOwnerId(50L)).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRepository.findById(999L)).thenReturn(Optional.empty());

        RestaurantOrderRequestDto req = new RestaurantOrderRequestDto(OrderStatus.ACCEPTED, null, null);
        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurantOrder(999L, req, 50L));
    }

    @Test
    void getRestaurantOrder_success_returnsOrder() {
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(100L);
        when(restaurantOrderRepository.findById(100L)).thenReturn(Optional.of(order));

        RestaurantOrder result = restaurantService.getRestaurantOrder(100L);

        assertNotNull(result);
        assertEquals(100L, result.getOrderId());
    }

    @Test
    void getRestaurantOrder_notFound_throws() {
        when(restaurantOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.getRestaurantOrder(999L));
    }

    @Test
    void getRestaurantOrder_nullOrderId_throws() {
        assertThrows(IllegalArgumentException.class, () -> restaurantService.getRestaurantOrder(null));
    }
}
