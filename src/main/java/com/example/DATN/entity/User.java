package com.example.DATN.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    private Integer userId; // Trùng với account.userId

    @ElementCollection
    @CollectionTable(name = "user_travel_styles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "style")
    private List<String> travelStyles; // Phong cách: khám phá, nghỉ dưỡng...

    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private List<String> interests; // Sở thích cụ thể: nhiếp ảnh, cà phê...

    @Column(name = "travel_budget")
    private String budget; // Ngân sách: tiết kiệm, trung bình, cao cấp

    @ElementCollection
    @CollectionTable(name = "user_companions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "companion")
    private List<String> companions; // Bạn đồng hành: một mình, gia đình...

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Account account;
}
