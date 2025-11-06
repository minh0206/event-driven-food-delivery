package com.fooddelivery.deliveryservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId; // Foreign key linking to the user in user-service

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    private Double currentLatitude;

    private Double currentLongitude;

    private Long currentOrderId; // This will be set when a driver is assigned an order
}