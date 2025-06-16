package com.example.graduation.dto.thesis;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubmittedThesisDTO {

    @NotBlank(message = "Thesis title cannot be empty")
    private String title;
}
