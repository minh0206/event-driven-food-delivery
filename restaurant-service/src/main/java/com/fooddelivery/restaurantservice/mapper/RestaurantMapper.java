package com.fooddelivery.restaurantservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.model.Restaurant;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    @Mapping(target = "restaurantName", source = "name")
    RestaurantDto toDto(Restaurant restaurant);
}
