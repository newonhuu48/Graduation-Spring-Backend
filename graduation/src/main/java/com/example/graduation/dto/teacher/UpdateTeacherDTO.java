package com.example.graduation.dto.teacher;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeacherDTO {

    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "First Name cannot be empty")
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(message = "Last Name cannot be empty")
    private String lastName;
}