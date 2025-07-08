package com.example.graduation.dto.thesis;

import com.example.graduation.entity.enums.ThesisStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubmittedThesisDTO {

    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Thesis title cannot be empty")
    private String title;

    private ThesisStatus status;

    private Long studentId;
}