package com.fooddelivery.userservice.mapper;

import org.mapstruct.Mapper;

import com.fooddelivery.userservice.dto.TokenDto;
import com.fooddelivery.userservice.model.Token;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    TokenDto toDto(Token token);
}
