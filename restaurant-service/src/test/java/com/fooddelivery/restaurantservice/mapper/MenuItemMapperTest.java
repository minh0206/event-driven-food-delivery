package com.fooddelivery.restaurantservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.model.MenuItem;

class MenuItemMapperTest {

    private final MenuItemMapper mapper = Mappers.getMapper(MenuItemMapper.class);

    @Test
    void toDto_mapsMenuItemToDto() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setName("Burger");
        menuItem.setDescription("Delicious beef burger");
        menuItem.setPrice(new BigDecimal("12.99"));

        // Act
        MenuItemDto dto = mapper.toDto(menuItem);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Burger", dto.name());
        assertEquals("Delicious beef burger", dto.description());
        assertEquals(new BigDecimal("12.99"), dto.price());
    }

    @Test
    void toDto_withNullDescription_mapsCorrectly() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        menuItem.setId(2L);
        menuItem.setName("Fries");
        menuItem.setDescription(null);
        menuItem.setPrice(new BigDecimal("4.50"));

        // Act
        MenuItemDto dto = mapper.toDto(menuItem);

        // Assert
        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals("Fries", dto.name());
        assertEquals(null, dto.description());
        assertEquals(new BigDecimal("4.50"), dto.price());
    }

    @Test
    void toDto_withZeroPrice_mapsCorrectly() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        menuItem.setId(3L);
        menuItem.setName("Free Sample");
        menuItem.setDescription("Complimentary item");
        menuItem.setPrice(BigDecimal.ZERO);

        // Act
        MenuItemDto dto = mapper.toDto(menuItem);

        // Assert
        assertNotNull(dto);
        assertEquals(3L, dto.id());
        assertEquals("Free Sample", dto.name());
        assertEquals("Complimentary item", dto.description());
        assertEquals(BigDecimal.ZERO, dto.price());
    }
}
