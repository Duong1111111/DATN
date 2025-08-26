package com.example.DATN.entity;

import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Action;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adId;
    private String title;
    private String description;
    // Cho phép nhiều action
    @ElementCollection(targetClass = Action.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "ad_actions",
            joinColumns = @JoinColumn(name = "ad_id")
    )
    @Column(name = "action")
    private List<Action> actions = new ArrayList<>();

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double budget;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany
    @JoinTable(
            name = "ad_category",
            joinColumns = @JoinColumn(name = "ad_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
    // Người tạo quảng cáo (Account - có thể là Admin hoặc Company)
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy;

    // Địa điểm được quảng cáo
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
