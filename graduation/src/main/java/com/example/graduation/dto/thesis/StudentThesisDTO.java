package com.example.graduation.dto.thesis;

import com.example.graduation.entity.enums.Grade;
import com.example.graduation.entity.enums.ThesisStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentThesisDTO {

    private Long id;
    private String title;

    private ThesisStatus status;

    private Grade grade; // May be null if not yet graded
}
