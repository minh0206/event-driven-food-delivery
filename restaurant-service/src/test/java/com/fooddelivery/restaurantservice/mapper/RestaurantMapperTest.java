package com.fooddelivery.restaurantservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.fooddelivery.restaurantservice.dto.RestaurantDto;
import com.fooddelivery.restaurantservice.model.Restaurant;

class RestaurantMapperTest {

    private final RestaurantMapper mapper = Mappers.getMapper(RestaurantMapper.class);

    @Test
    void toDto_mapsRestaurantToDto() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("123 Main St");
        restaurant.setCuisineType("Italian");
        restaurant.setOwnerId(50L);

        // Act
        RestaurantDto dto = mapper.toDto(restaurant);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Test Restaurant", dto.restaurantName());
        assertEquals("123 Main St", dto.address());
        assertEquals("Italian", dto.cuisineType());
    }

    @Test
    void toDto_withNullAddress_mapsCorrectly() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(2L);
        restaurant.setName("Another Restaurant");
        restaurant.setAddress(null);
        restaurant.setCuisineType("Chinese");

        // Act
        RestaurantDto dto = mapper.toDto(restaurant);

        // Assert
        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals("Another Restaurant", dto.restaurantName());
        assertEquals(null, dto.address());
        assertEquals("Chinese", dto.cuisineType());
    }

    @Test
    void toDto_withNullCuisineType_mapsCorrectly() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(3L);
        restaurant.setName("Generic Restaurant");
        restaurant.setAddress("456 Oak Ave");
        restaurant.setCuisineType(null);

        // Act
        RestaurantDto dto = mapper.toDto(restaurant);

        // Assert
        assertNotNull(dto);
        assertEquals(3L, dto.id());
        assertEquals("Generic Restaurant", dto.restaurantName());
        assertEquals("456 Oak Ave", dto.address());
        assertEquals(null, dto.cuisineType());
    }
}
