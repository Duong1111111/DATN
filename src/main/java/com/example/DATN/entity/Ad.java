package com.example.DATN.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adId;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double budget;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Người tạo quảng cáo (Account - có thể là Admin hoặc Company)
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy;

    // Địa điểm được quảng cáo
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
