package com.fooddelivery.restaurantservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurantservice.dto.CompleteOrderViewDto;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderRequestDto;
import com.fooddelivery.restaurantservice.mapper.RestaurantOrderMapper;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.restaurantservice.model.RestaurantOrderItem;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import com.fooddelivery.shared.dto.MasterOrderDto;
import com.fooddelivery.shared.dto.RestaurantRequestDto;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.OrderAcceptedEvent;
import com.fooddelivery.shared.event.OrderPlacedEvent;
import com.fooddelivery.shared.event.OrderReadyEvent;
import com.fooddelivery.shared.event.OrderRejectedEvent;
import com.fooddelivery.shared.publisher.OrderAcceptedEventPublisher;
import com.fooddelivery.shared.publisher.OrderReadyEventPublisher;
import com.fooddelivery.shared.publisher.OrderRejectedEventPublisher;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class RestaurantService {
    private static final String RESTAURANT_NOT_FOUND_MESSAGE = "Restaurant not found";
    private static final String RESTAURANT_ORDER_NOT_FOUND_MESSAGE = "Restaurant order not found";
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final OrderAcceptedEventPublisher orderAcceptedEventPublisher;
    private final OrderReadyEventPublisher orderReadyEventPublisher;
    private final OrderRejectedEventPublisher orderRejectedEventPublisher;
    private final OrderServiceClient orderServiceClient;
    private final RestaurantOrderMapper restaurantOrderMapper;

    public Restaurant createRestaurant(RestaurantRequestDto requestDto, Long ownerId) {
        if (restaurantRepository.findByOwnerId(ownerId).isPresent()) {
            throw new EntityExistsException("User already owns a restaurant.");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setName(requestDto.restaurantName());
        restaurant.setAddress(requestDto.address());
        restaurant.setCuisineType(requestDto.cuisineType());
        restaurant.setOwnerId(ownerId);

        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long restaurantId, RestaurantRequestDto requestDto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

        // **CRITICAL** Authorization check
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("User is not authorized to update this restaurant");
        }

        restaurant.setName(requestDto.restaurantName());
        restaurant.setAddress(requestDto.address());
        restaurant.setCuisineType(requestDto.cuisineType());

        return restaurantRepository.save(restaurant);
    }

    public MenuItem addMenuItem(Long restaurantId, MenuItemRequestDto requestDto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

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
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

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
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

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
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));
    }

    public Restaurant getRestaurantByOwnerId(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));
    }

    @Transactional
    public List<MenuItem> getRestaurantMenu(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));
        return new ArrayList<>(restaurant.getMenu());
    }

    public List<RestaurantOrder> getRestaurantOrders(Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

        return restaurantOrderRepository.findByRestaurantIdOrderByReceivedAtDesc(restaurant.getId());
    }

    public void createRestaurantOrder(OrderPlacedEvent event) {
        RestaurantOrder restaurantOrder = new RestaurantOrder();
        restaurantOrder.setOrderId(event.orderId());
        restaurantOrder.setRestaurantId(event.restaurantId());
        restaurantOrder.setLocalStatus(OrderStatus.PENDING);
        restaurantOrder.setReceivedAt(LocalDateTime.now());

        List<RestaurantOrderItem> orderItems = event.items().stream().map(item -> {
            RestaurantOrderItem orderItem = new RestaurantOrderItem();
            orderItem.setOrderItemId(item.orderItemId());
            orderItem.setMenuItemId(item.menuItemId());
            orderItem.setQuantity(item.quantity());
            orderItem.setOrder(restaurantOrder);
            return orderItem;
        }).toList();

        restaurantOrder.setItems(orderItems);

        restaurantOrderRepository.save(restaurantOrder);
    }

    public RestaurantOrder updateRestaurantOrder(Long orderId, RestaurantOrderRequestDto requestDto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

        if (orderId == null) {
            throw new IllegalArgumentException("orderId must not be null");
        }
        RestaurantOrder orderToUpdate = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // **CRITICAL** Authorization check
        if (!restaurant.getId().equals(orderToUpdate.getRestaurantId())) {
            throw new SecurityException("User is not authorized to modify this order");
        }

        // Check request status is in (ACCEPTED, REJECTED, READY_FOR_PICKUP)
        if (!List.of(OrderStatus.ACCEPTED, OrderStatus.REJECTED, OrderStatus.READY_FOR_PICKUP)
                .contains(requestDto.status())) {
            throw new IllegalArgumentException("Invalid order status");
        }

        orderToUpdate.setLocalStatus(requestDto.status());
        orderToUpdate.setAssignedCook(requestDto.assignedCook());
        orderToUpdate.setInternalNotes(requestDto.internalNotes());

        var updatedOrder = restaurantOrderRepository.save(orderToUpdate);

        if (updatedOrder.getLocalStatus() == OrderStatus.ACCEPTED) {
            // Publish order accepted event
            OrderAcceptedEvent acceptedEvent = new OrderAcceptedEvent(
                    orderId,
                    restaurant.getId());
            orderAcceptedEventPublisher.publish(acceptedEvent);
        } else if (updatedOrder.getLocalStatus() == OrderStatus.READY_FOR_PICKUP) {
            // Publish order ready event
            OrderReadyEvent readyEvent = new OrderReadyEvent(orderId);
            orderReadyEventPublisher.publish(readyEvent);
        } else if (updatedOrder.getLocalStatus() == OrderStatus.REJECTED) {
            // Publish order rejected event
            OrderRejectedEvent rejectedEvent = new OrderRejectedEvent(orderId);
            orderRejectedEventPublisher.publish(rejectedEvent);
        }

        return updatedOrder;
    }

    public RestaurantOrder getRestaurantOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId must not be null");
        }
        return restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_ORDER_NOT_FOUND_MESSAGE));
    }

    public List<RestaurantOrder> getActiveOrderDetails(Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

        // Find orders that are NOT in a completed state.
        return restaurantOrderRepository.findByRestaurantIdAndLocalStatusNot(restaurant.getId(), OrderStatus.DELIVERED);
    }

    public Page<CompleteOrderViewDto> getHistoricalOrderDetails(Long ownerId, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("pageable must not be null");
        }

        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(RESTAURANT_NOT_FOUND_MESSAGE));

        // FETCH MASTER DATA from order-service
        Page<MasterOrderDto> masterOrderData = null;
        try {
            masterOrderData = orderServiceClient
                    .getHistoricalOrdersByRestaurantId(restaurant.getId(), pageable);
        } catch (Exception e) {
            log.error("Error fetching masterOrderData: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // FETCH OPERATIONAL DATA from the local database
        Page<RestaurantOrder> operationalData = restaurantOrderRepository
                .findByRestaurantIdAndLocalStatus(restaurant.getId(), OrderStatus.DELIVERED, pageable);

        if (operationalData.getTotalElements() != masterOrderData.getTotalElements()) {
            String errorMessage = String.format("operationalData and masterOrderData must have the same size. " +
                    "operationalData: %s, masterOrderData: %s", operationalData.getTotalElements(),
                    masterOrderData.getTotalElements());

            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<CompleteOrderViewDto> completeData = new ArrayList<>();
        for (int i = 0; i < masterOrderData.getTotalElements(); i++) {
            MasterOrderDto masterOrder = masterOrderData.getContent().get(i);
            RestaurantOrder operationalOrder = operationalData.getContent().get(i);
            completeData.add(restaurantOrderMapper.toCompleteOrderViewDto(masterOrder, operationalOrder));
        }

        // This simulates a standard Spring Data Page response for your API controller.
        return new PageImpl<>(
                completeData,
                pageable, // The original request parameters
                completeData.size() // The total count of all available items
        );
    }
}
