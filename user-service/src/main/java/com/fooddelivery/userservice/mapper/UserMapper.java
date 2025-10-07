package com.fooddelivery.userservice.mapper;

import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
