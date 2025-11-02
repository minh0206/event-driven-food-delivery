package com.fooddelivery.userservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DriverDto extends UserDto {
    private String driverId;
}
