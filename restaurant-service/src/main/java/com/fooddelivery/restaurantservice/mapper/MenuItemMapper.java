package com.fooddelivery.restaurantservice.mapper;

import org.mapstruct.Mapper;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.model.MenuItem;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemDto toDto(MenuItem menuItem);
}
