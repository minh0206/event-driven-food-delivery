package com.fooddelivery.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fooddelivery.userservice.dto.RefreshTokenDto;
import com.fooddelivery.userservice.model.RefreshToken;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userRole", source = "user.role")
    RefreshTokenDto toDto(RefreshToken token);
}
