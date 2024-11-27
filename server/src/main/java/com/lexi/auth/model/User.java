package com.lexi.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String email; // Add email field to match Flyway

    @Getter @Setter
    private String password;

    @Getter @Setter
    @Enumerated(EnumType.STRING) // Map ENUM to Java Enum
    private Role role;

    @Getter @Setter
    private String refreshToken;

    @Column(name = "created_at", updatable = false)
    @Getter @Setter
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Getter @Setter
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }
}
