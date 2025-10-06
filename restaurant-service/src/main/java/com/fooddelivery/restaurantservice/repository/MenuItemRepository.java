package com.fooddelivery.restaurantservice.repository;

import com.fooddelivery.restaurantservice.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // No custom methods needed for now, basic CRUD is enough.
}