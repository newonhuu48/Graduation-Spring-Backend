package com.example.graduation.entity;

import com.example.graduation.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student extends BaseEntity {


    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String studentNumber; // e.g., university ID


    //The thesis of the student
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Thesis thesis;


    //Associate with Profile
    @OneToOne(mappedBy = "student_owner", fetch = FetchType.LAZY)
    private User profile;


}