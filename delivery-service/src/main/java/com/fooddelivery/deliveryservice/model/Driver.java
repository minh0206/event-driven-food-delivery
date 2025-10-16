package com.fooddelivery.deliveryservice.model;

import jakarta.persistence.*;
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

    // This will be set when a driver is assigned an order
    private Long currentOrderId;
}