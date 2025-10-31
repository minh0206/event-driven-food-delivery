package com.fooddelivery.restaurantservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.restaurantservice.model.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}