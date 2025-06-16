package com.example.graduation.entity;

import com.example.graduation.entity.common.BaseEntity;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.entity.enums.ThesisStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "thesis")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Thesis extends BaseEntity {


    @Column(nullable = false)
    private String title;


    //STATUS - Submitted, Approved, Defended, Rejected
    @Enumerated(EnumType.STRING)
    private ThesisStatus status;


    //Thesis AUTHOR - Student
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", unique = true)
    private Student student;


    @Enumerated(EnumType.STRING) // important! store enum as string in DB
    private Grade grade;
}
