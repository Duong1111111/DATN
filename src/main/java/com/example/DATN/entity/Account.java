package com.example.DATN.entity;

import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String username;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Quan hệ 1-1 với User (nếu là người dùng)
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private User user;

    // Quan hệ 1-1 với Company (nếu là công ty)
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Company company;

    @OneToMany(mappedBy = "user") // trong Review -> Account user
    private List<Review> reviews;

    @OneToMany(mappedBy = "user") // trong Favorite -> Account user
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "createdBy") // trong Location -> Account createdBy
    private List<Location> createdLocations;

    @OneToMany(mappedBy = "createdBy") // trong Ad -> Account createdBy
    private List<Ad> ads;
}
