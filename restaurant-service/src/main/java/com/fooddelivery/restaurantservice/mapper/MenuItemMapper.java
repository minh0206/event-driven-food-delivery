package com.fooddelivery.restaurantservice.mapper;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.model.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    @Mapping(source = "restaurant.id", target = "restaurantId")
    MenuItemDto toDto(MenuItem menuItem);
}
