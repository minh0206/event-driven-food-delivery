package com.fooddelivery.restaurantservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fooddelivery.restaurantservice.dto.RestaurantOrderDto;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;

@Mapper(componentModel = "spring")
public interface RestaurantOrderMapper {
    @Mapping(target = "status", source = "localStatus")
    RestaurantOrderDto toDto(RestaurantOrder restaurantOrder);
}
