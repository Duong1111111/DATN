package com.example.DATN.entity;

import com.example.DATN.utils.enums.options.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationId;

    private String name;
    private String description;
    private String location;
    private Double price;
    private LocalTime openTime;
    private LocalTime closeTime;
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    private List<LocationImage> images = new ArrayList<>();
    private String website;
    private Integer phoneNumber;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Relationships
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;

}
