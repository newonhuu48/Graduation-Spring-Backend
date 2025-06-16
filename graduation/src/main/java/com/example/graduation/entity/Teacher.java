package com.example.graduation.entity;

import com.example.graduation.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teacher")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Teacher extends BaseEntity {


    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;


    @Column(unique = true, nullable = false)
    private String teacherNumber; // e.g., university ID


    //Associate with Profile
    @OneToOne(mappedBy = "teacher_owner")
    private User profile;


}