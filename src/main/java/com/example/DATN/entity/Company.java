package com.example.DATN.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Companies")
public class Company {
    @Id
    private Integer userId; // Trùng với account.userId

    private String companyName;
    private String taxCode;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Account account;
}
