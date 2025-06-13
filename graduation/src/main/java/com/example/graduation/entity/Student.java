package com.example.graduation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;


    @Column(unique = true, nullable = false)
    private String studentNumber; // e.g., university ID


    //Associate with Profile
    @OneToOne(mappedBy = "student_owner", fetch = FetchType.LAZY)
    private User profile;
}