package com.example.DATN.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String location;
    private String phoneNumber;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private Account account;
}
