package com.fooddelivery.restaurantservice.mapper;

import org.mapstruct.Mapper;

import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.model.Restaurant;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    RestaurantDto toDto(Restaurant restaurant);
}
