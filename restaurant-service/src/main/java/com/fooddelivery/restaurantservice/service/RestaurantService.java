package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.dto.RestaurantRequestDto;
import com.fooddelivery.restaurantservice.mapper.MenuItemMapper;
import com.fooddelivery.restaurantservice.mapper.RestaurantMapper;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    public RestaurantDto createRestaurant(RestaurantRequestDto requestDto, Long ownerId) {
        if (restaurantRepository.findByOwnerId(ownerId).isPresent()) {
            throw new IllegalStateException("User already owns a restaurant.");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setName(requestDto.name());
        restaurant.setAddress(requestDto.address());
        restaurant.setCuisineType(requestDto.cuisineType());
        restaurant.setOwnerId(ownerId);

        restaurantRepository.save(restaurant);
        return restaurantMapper.toDto(restaurant);
    }

    public RestaurantDto updateRestaurant(Long restaurantId, RestaurantRequestDto requestDto, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // **CRITICAL** Authorization check
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new SecurityException("User is not authorized to update this restaurant");
        }

        restaurant.setName(requestDto.name());
        restaurant.setAddress(requestDto.address());
        restaurant.setCuisineType(requestDto.cuisineType());

        restaurantRepository.save(restaurant);
        return restaurantMapper.toDto(restaurant);
    }

    public MenuItemDto addMenuItem(Long restaurantId, MenuItemRequestDto requestDto, Long ownerId) {
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

        menuItemRepository.save(menuItem);
        return menuItemMapper.toDto(menuItem);
    }

    public MenuItemDto updateMenuItem(Long restaurantId, Long itemId, MenuItemRequestDto dto, Long ownerId) {
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

        menuItemRepository.save(menuItem);
        return menuItemMapper.toDto(menuItem);
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

    public Page<RestaurantDto> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable)
                .map(restaurantMapper::toDto);
    }

    public RestaurantDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        return restaurantMapper.toDto(restaurant);
    }

    @Transactional
    public List<MenuItemDto> getRestaurantMenu(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        return restaurant.getMenu()
                .stream()
                .map(menuItemMapper::toDto)
                .toList();
    }
}
