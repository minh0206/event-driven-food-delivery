package com.fooddelivery.restaurantservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.OrderAcceptedEvent;
import com.fooddelivery.shared.event.OrderStatusUpdateEvent;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RestaurantService {
    private static final String TOPIC_ORDER_STATUS = "order_status_updates";
    private static final String TOPIC_ORDER_ACCEPTED = "order_accepted";

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final KafkaTemplate<String, OrderStatusUpdateEvent> orderStatusUpdateKafkaTemplate;
    private final KafkaTemplate<String, OrderAcceptedEvent> orderAcceptedKafkaTemplate;

    public Restaurant createRestaurant(RestaurantRequestDto requestDto, Long ownerId) {
        if (restaurantRepository.findByOwnerId(ownerId).isPresent()) {
            throw new IllegalStateException("User already owns a restaurant.");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setName(requestDto.name());
        restaurant.setAddress(requestDto.address());
        restaurant.setCuisineType(requestDto.cuisineType());
        restaurant.setOwnerId(ownerId);

        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long restaurantId, RestaurantRequestDto requestDto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // **CRITICAL** Authorization check
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("User is not authorized to update this restaurant");
        }

        restaurant.setName(requestDto.name());
        restaurant.setAddress(requestDto.address());
        restaurant.setCuisineType(requestDto.cuisineType());

        return restaurantRepository.save(restaurant);
    }

    public MenuItem addMenuItem(Long restaurantId, MenuItemRequestDto requestDto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // **CRITICAL** Authorization check
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("User is not authorized to modify this menu");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(requestDto.name());
        menuItem.setDescription(requestDto.description());
        menuItem.setPrice(requestDto.price());
        menuItem.setRestaurant(restaurant);

        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Long restaurantId, Long itemId, MenuItemRequestDto dto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // **CRITICAL** Authorization check
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("User is not authorized to modify this menu");
        }

        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found"));

        menuItem.setName(dto.name());
        menuItem.setDescription(dto.description());
        menuItem.setPrice(dto.price());

        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long restaurantId, Long itemId, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // **CRITICAL** Authorization check
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("User is not authorized to modify this menu");
        }

        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem not found"));

        menuItemRepository.delete(menuItem);
    }

    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
    }

    public Restaurant getRestaurantByOwnerId(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
    }

    @Transactional
    public List<MenuItem> getRestaurantMenu(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        return restaurant.getMenu();
    }

    public void updateOrderStatus(Long orderId, Long ownerId, OrderStatus status) {
        // TODO: Add logic to find the restaurant by ownerId and verify this order
        // belongs to them.
        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        OrderStatusUpdateEvent statusUpdateEvent = new OrderStatusUpdateEvent(orderId, status);
        orderStatusUpdateKafkaTemplate.send(TOPIC_ORDER_STATUS, statusUpdateEvent);

        if (status != OrderStatus.ACCEPTED)
            return;

        // TODO: Implement restaurant latitude and longitude
        OrderAcceptedEvent acceptedEvent = new OrderAcceptedEvent(
                orderId,
                restaurant.getId(),
                1F,
                1F);
        orderAcceptedKafkaTemplate.send(TOPIC_ORDER_ACCEPTED, acceptedEvent);
    }
}
