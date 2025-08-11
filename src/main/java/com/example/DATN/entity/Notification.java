package com.example.DATN.entity;

import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.options.Type;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String content;
    private LocalDateTime createdAt;
    private boolean readStatus = false;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Account receiver;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    @Enumerated(EnumType.STRING)
    private Role targetRole;
}
