package com.fooddelivery.deliveryservice.mapper;

import org.mapstruct.Mapper;

import com.fooddelivery.deliveryservice.dto.DriverDto;
import com.fooddelivery.deliveryservice.model.Driver;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    DriverDto toDto(Driver driver);
}