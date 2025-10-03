package com.fooddelivery.userservice.mapper;

import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequestDto registerRequestDto);
}
