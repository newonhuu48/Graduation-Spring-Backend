package com.example.graduation.unit;

import com.example.graduation.dto.thesis.DefendedThesisDTO;
import com.example.graduation.dto.thesis.CreateDefendedThesisDTO;
import com.example.graduation.dto.thesis.UpdateDefendedThesisDTO;
import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.entity.enums.ThesisStatus;
import com.example.graduation.repository.ThesisRepository;
import com.example.graduation.service.ThesisService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DefendedThesisServiceTest {

    @Mock
    ThesisRepository thesisRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ThesisService thesisService;

    Thesis thesis;
    Pageable pageable;


    @BeforeEach
    void setup() {
        thesis = new Thesis();
        thesis.setId(1L);
        thesis.setTitle("Defended Thesis");
        thesis.setStatus(ThesisStatus.DEFENDED);
        thesis.setGrade(Grade.GRADE_4_50);

        pageable = PageRequest.of(0, 10);
    }


    //Get all Defended Theses
    @Test
    void getAllDefendedThesesTest() {
        // Prepare a page of theses
        Page<Thesis> thesisPage = new PageImpl<>(List.of(thesis), pageable, 1);

        when(thesisRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(thesisPage);

        // Stub convert method
        when(modelMapper.map(any(), ArgumentMatchers.eq(DefendedThesisDTO.class)))
                .thenAnswer(invocation -> {
                    Thesis t = invocation.getArgument(0);
                    DefendedThesisDTO dto = new DefendedThesisDTO();
                    dto.setGrade(t.getGrade());
                    dto.setTitle(t.getTitle());
                    return dto;
                });

        Page<DefendedThesisDTO> result = thesisService.getAllDefendedTheses(
                null, null, Grade.GRADE_4_50, 0, 10, "title", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals("Defended Thesis", result.getContent().get(0).getTitle());

        verify(thesisRepository).findAll(any(Specification.class), any(Pageable.class));
    }


    //Update Defended Thesis
    //Success
    @Test
    void updateDefendedThesisSuccessTest() {
        UpdateDefendedThesisDTO updateDTO = new UpdateDefendedThesisDTO();
        updateDTO.setGrade(Grade.GRADE_4_00);

        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));
        when(thesisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(), ArgumentMatchers.eq(DefendedThesisDTO.class))).thenAnswer(invocation -> {

            Thesis thesis = invocation.getArgument(0);
            DefendedThesisDTO dto = new DefendedThesisDTO();
            dto.setGrade(thesis.getGrade());

            return dto;
        });


        DefendedThesisDTO result = thesisService.updateDefendedThesis(1L, updateDTO);

        assertEquals(Grade.GRADE_4_00, result.getGrade());
        verify(thesisRepository).save(any());
    }

    //Throw if Defended Thesis not found
    @Test
    void updateDefendedThesisNotFoundTest() {
        when(thesisRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateDefendedThesisDTO updateDTO = new UpdateDefendedThesisDTO();
        updateDTO.setGrade(Grade.GRADE_4_00);

        assertThrows(EntityNotFoundException.class, () ->
                thesisService.updateDefendedThesis(1L, updateDTO));
    }



    //Get Defended Thesis By ID
    //Success
    @Test
    void getDefendedThesisByIdSuccessTest() {

        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));
        when(modelMapper.map(any(), ArgumentMatchers.eq(UpdateDefendedThesisDTO.class)))
                .thenReturn(new UpdateDefendedThesisDTO());

        UpdateDefendedThesisDTO dto = thesisService.getDefendedThesisById(1L);

        assertNotNull(dto);
        verify(thesisRepository).findById(1L);
    }

    //Throw Exception if Thesis is with wrong Status
    @Test
    void getDefendedThesisByIdThrowWrongStatusTest() {
        thesis.setStatus(ThesisStatus.APPROVED); //Intentionally wrong status of Thesis

        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));

        assertThrows(IllegalStateException.class, () ->
                thesisService.getDefendedThesisById(1L));
    }

    //Throw Exception if not found
    @Test
    void getDefendedThesisByThrowTest() {
        when(thesisRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                thesisService.getDefendedThesisById(1L));
    }



    //Change Thesis Status from Approved -> Defended
    //Success
    @Test
    void defendThesisSuccessTest() {
        CreateDefendedThesisDTO createDTO = new CreateDefendedThesisDTO();
        createDTO.setGrade(Grade.GRADE_4_00);

        thesis.setStatus(ThesisStatus.APPROVED);

        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));
        when(thesisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(), ArgumentMatchers.eq(CreateDefendedThesisDTO.class))).thenReturn(createDTO);

        CreateDefendedThesisDTO result = thesisService.defendThesis(1L, createDTO);

        assertEquals(Grade.GRADE_4_00, result.getGrade());
        verify(thesisRepository).save(any());
    }


    //Throw if Thesis not found
    @Test
    void defendThesisNotFoundTest() {
        when(thesisRepository.findById(1L)).thenReturn(Optional.empty());

        CreateDefendedThesisDTO createDTO = new CreateDefendedThesisDTO();
        createDTO.setGrade(Grade.GRADE_4_00);

        assertThrows(EntityNotFoundException.class, () ->
                thesisService.defendThesis(1L, createDTO));
    }



    //Delete Thesis
    //Success
    @Test
    void deleteThesisSuccessTest() {

        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));

        thesisService.deleteThesis(1L);

        verify(thesisRepository).findById(1L);
        verify(thesisRepository).delete(thesis);
    }

    //If Thesis not found
    @Test
    void deleteThesisNotFoundTest() {
        // Arrange
        when(thesisRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> thesisService.deleteThesis(1L));
    }
}