package com.example.graduation.unit.service;


import com.example.graduation.dto.thesis.ApprovedThesisDTO;
import com.example.graduation.entity.Student;
import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.enums.ThesisStatus;
import com.example.graduation.repository.ThesisRepository;
import com.example.graduation.service.ThesisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApprovedThesisServiceTest {

    @Mock
    private ThesisRepository thesisRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ThesisService thesisService;

    private Thesis thesis;

    private Student student;

    @BeforeEach
    void setup() {
        student = new Student();
        student.setId(1L);
        student.setStudentNumber("F123456");

        thesis = new Thesis();
        thesis.setId(10L);
        thesis.setTitle("Approved Thesis");
        thesis.setStudent(student);
        thesis.setStatus(ThesisStatus.APPROVED);
    }

    @Test
    void getAllApprovedTheses_returnsFilteredPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Thesis> thesisPage = new PageImpl<>(List.of(thesis));
        ApprovedThesisDTO dto = new ApprovedThesisDTO();
        dto.setTitle("Approved Thesis");

        when(thesisRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(thesisPage);
        when(modelMapper.map(thesis, ApprovedThesisDTO.class)).thenReturn(dto);

        // Act
        Page<ApprovedThesisDTO> result = thesisService.getAllApprovedTheses("Approved", "F123456", 0, 10, "title", "asc");

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Approved Thesis", result.getContent().get(0).getTitle());
    }
}
