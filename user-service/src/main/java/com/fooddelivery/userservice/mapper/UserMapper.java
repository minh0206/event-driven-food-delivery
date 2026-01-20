package com.fooddelivery.userservice.mapper;

import org.mapstruct.Mapper;

import com.fooddelivery.userservice.dto.CustomerDto;
import com.fooddelivery.userservice.dto.DriverDto;
import com.fooddelivery.userservice.dto.RestaurantAdminDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    CustomerDto toCustomerDto(User user);

    RestaurantAdminDto toRestaurantAdminDto(User user);

    DriverDto toDriverDto(User user);
}
