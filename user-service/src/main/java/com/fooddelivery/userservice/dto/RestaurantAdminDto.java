package com.fooddelivery.userservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RestaurantAdminDto extends CustomerDto {
    private Long restaurantId;
}
