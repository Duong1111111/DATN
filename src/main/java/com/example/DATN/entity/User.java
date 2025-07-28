package com.example.DATN.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    private Integer userId; // Trùng với account.userId

    private String hobby;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Account account;
}
