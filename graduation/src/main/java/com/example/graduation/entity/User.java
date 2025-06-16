package com.example.graduation.entity;

import com.example.graduation.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User
        extends BaseEntity
        implements UserDetails {


    private String username;
    private String password;


    //ROLES THAT THE USER HAS
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;


    //GET ROLES OVERRIDE
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }


    //Associate with - Teacher OR Student OR Neither
    //Teacher
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher_owner;


    //Student
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student_owner;

}