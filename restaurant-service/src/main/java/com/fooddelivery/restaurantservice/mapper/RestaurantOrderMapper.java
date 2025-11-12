package com.fooddelivery.restaurantservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fooddelivery.restaurantservice.dto.CompleteOrderViewDto;
import com.fooddelivery.restaurantservice.dto.RestaurantOrderDto;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.shared.dto.MasterOrderDto;

@Mapper(componentModel = "spring")
public interface RestaurantOrderMapper {
    @Mapping(target = "status", source = "localStatus")
    RestaurantOrderDto toDto(RestaurantOrder restaurantOrder);

    @Mapping(target = "orderId", source = "masterOrder.orderId")
    @Mapping(target = "restaurantId", source = "masterOrder.restaurantId")
    @Mapping(target = "items", source = "masterOrder.items")
    CompleteOrderViewDto toCompleteOrderViewDto(MasterOrderDto masterOrder, RestaurantOrder operationalOrder);
}
