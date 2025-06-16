package com.example.graduation.dto.student;

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
public class StudentDTO {

    private Long id;


    @NotEmpty(message = "First Name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last Name cannot be empty")
    private String lastName;


    @NotEmpty(message = "Faculty number cannot be empty")
    @Pattern(regexp = "^[F]?[0-9]{5,10}$", message = "Student number must be valid (5-10 digits)")
    private String studentNumber; // e.g., university ID
}
