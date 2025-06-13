package com.example.graduation.dto.teacher;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeacherDTO {

    @Column(nullable = false)
    @NotEmpty(message = "First Name cannot be empty")
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(message = "Last Name cannot be empty")
    private String lastName;

    @Column(unique = true, nullable = false)
    @NotEmpty(message = "Faculty number cannot be empty")
    @Pattern(regexp = "^[F]?[0-9]{5,10}$", message = "Invalid Teacher Number (Teacher Number must be 5-10 digits)")
    private String teacherNumber; // e.g., university ID
}