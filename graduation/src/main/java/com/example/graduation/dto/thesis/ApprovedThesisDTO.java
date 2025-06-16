package com.example.graduation.dto.thesis;

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
public class ApprovedThesisDTO {

    private Long id;

    private String title;


    private Long studentId;
    private String studentNumber;
}
