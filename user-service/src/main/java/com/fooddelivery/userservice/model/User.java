package com.fooddelivery.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // This will store the hashed password

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
