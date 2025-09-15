package com.example.DATN.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ad_action_log")
@Getter
@Setter
public class AdActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Column(nullable = false)
    private String actionType; // "IMPRESSION" hoặc "CLICK"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account user;

    @Column(nullable = false)
    private LocalDateTime actionTimestamp;

    @PrePersist
    protected void onCreateAction() {
        this.actionTimestamp = LocalDateTime.now();
    }
}