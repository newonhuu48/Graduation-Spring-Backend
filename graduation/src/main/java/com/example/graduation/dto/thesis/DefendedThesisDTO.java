package com.example.graduation.dto.thesis;

import com.example.graduation.entity.enums.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefendedThesisDTO {

    private Long id;
    private String title;

    private Long studentId;
    private String studentNumber;

    private Grade grade; // May be null if not yet graded
}
