package com.example.DATN.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "location_view_log")
@Getter
@Setter
public class LocationViewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Nullable cho khách vãng lai
    private Account user;

    @Column(nullable = false)
    private LocalDateTime viewTimestamp;

    @PrePersist
    protected void onCreateView() {
        this.viewTimestamp = LocalDateTime.now();
    }
}