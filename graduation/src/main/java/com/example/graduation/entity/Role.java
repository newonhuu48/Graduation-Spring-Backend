package com.example.graduation.entity;

import com.example.graduation.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role
        extends BaseEntity
        implements GrantedAuthority {


    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }

}