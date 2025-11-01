package com.fooddelivery.deliveryservice.model;

public enum DriverStatus {
    OFFLINE, // Not available for deliveries
    AVAILABLE, // Online and waiting for an order
    ON_DELIVERY // Currently assigned to and completing an order
}