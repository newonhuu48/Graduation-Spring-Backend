package com.example.graduation.unit.service;

import com.example.graduation.dto.thesis.ApprovedThesisDTO;
import com.example.graduation.dto.thesis.CreateSubmittedThesisDTO;
import com.example.graduation.dto.thesis.SubmittedThesisDTO;
import com.example.graduation.dto.thesis.UpdateSubmittedThesisDTO;
import com.example.graduation.entity.Student;
import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.User;
import com.example.graduation.entity.enums.ThesisStatus;
import com.example.graduation.exception.ThesisNotFoundException;
import com.example.graduation.repository.StudentRepository;
import com.example.graduation.repository.ThesisRepository;
import com.example.graduation.service.ThesisService;
import com.example.graduation.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmittedThesisServiceTest {

    @Mock
    private ThesisRepository thesisRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService; //convertToEntity() uses currentUser

    @InjectMocks
    private ThesisService thesisService;

    private Thesis thesis;


    @BeforeEach
    void setup() {
        thesis = new Thesis();
        thesis.setId(1L);
        thesis.setTitle("AI Thesis");
        thesis.setStatus(ThesisStatus.SUBMITTED);
    }


    //Get all Theses
    @Test
    void getAllSubmittedThesesTest() {
        Page<Thesis> page = new PageImpl<>(List.of(thesis));
        when(thesisRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<SubmittedThesisDTO> result = thesisService.getAllSubmittedTheses("AI", "123", 0, 10, "title", "asc");

        assertEquals(1, result.getTotalElements());
        verify(thesisRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }


    //Create Thesis
    @Test
    void submitThesisTest() {
        CreateSubmittedThesisDTO createDTO = new CreateSubmittedThesisDTO();
        createDTO.setTitle("AI Thesis");

        //The convertToEntity() inside submitThesis()
        //requires User info
        User mockUser = mock(User.class);
        Student mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setStudentNumber("F123456");

        when(thesisRepository.save(any())).thenReturn(thesis);

        SubmittedThesisDTO returnedDTO = new SubmittedThesisDTO();
        returnedDTO.setId(1L);
        returnedDTO.setTitle("AI Thesis");

        when(modelMapper.map(any(), eq(SubmittedThesisDTO.class))).thenReturn(returnedDTO);

        //User Mock
        when(mockUser.getStudent_owner()).thenReturn(mockStudent);
        when(userService.getCurrentUser()).thenReturn(mockUser);


        SubmittedThesisDTO result = thesisService.submitThesis(createDTO);

        assertEquals("AI Thesis", result.getTitle());
        verify(thesisRepository, times(1)).save(any());
    }


    //Update Thesis
    @Test
    void updateSubmittedThesisSuccessTest() {
        UpdateSubmittedThesisDTO updateDTO = new UpdateSubmittedThesisDTO();
        updateDTO.setTitle("Updated Title");

        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));
        when(thesisRepository.save(any())).thenReturn(thesis);

        SubmittedThesisDTO returnedDTO = new SubmittedThesisDTO();
        returnedDTO.setTitle("Updated Title");
        when(modelMapper.map(any(), eq(SubmittedThesisDTO.class))).thenReturn(returnedDTO);

        SubmittedThesisDTO result = thesisService.updateSubmittedThesis(1L, updateDTO);

        assertEquals("Updated Title", result.getTitle());
        verify(thesisRepository).save(any());
    }

    //If Thesis ID doesn't exist throw an exception
    @Test
    void updateSubmittedThesisThrowTest() {
        when(thesisRepository.findById(anyLong())).thenReturn(Optional.empty());
        UpdateSubmittedThesisDTO updateDTO = new UpdateSubmittedThesisDTO();

        assertThrows(ThesisNotFoundException.class, () -> thesisService.updateSubmittedThesis(99L, updateDTO));
    }


    //Get Thesis By ID if it exists
    @Test
    void getSubmittedThesisByIdSuccessTest() {
        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));

        UpdateSubmittedThesisDTO dto = new UpdateSubmittedThesisDTO();
        dto.setTitle("AI Thesis");

        when(modelMapper.map(any(), eq(UpdateSubmittedThesisDTO.class))).thenReturn(dto);

        UpdateSubmittedThesisDTO result = thesisService.getSubmittedThesisById(1L);

        assertEquals("AI Thesis", result.getTitle());
    }

    //If Thesis ID doesn't exist throw Exception
    @Test
    void getSubmittedThesisByIdThrowTest() {
        thesis.setStatus(ThesisStatus.APPROVED);
        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));

        assertThrows(IllegalStateException.class, () -> thesisService.getSubmittedThesisById(1L));
    }


    //If Thesis ID exists Change Thesis status
    //Submitted -> Approved
    @Test
    void approveThesisSuccessTest() {
        when(thesisRepository.findById(1L)).thenReturn(Optional.of(thesis));

        Thesis approved = new Thesis();
        approved.setId(1L);
        approved.setStatus(ThesisStatus.APPROVED);
        approved.setTitle("AI Thesis");

        when(thesisRepository.save(any())).thenReturn(approved);

        ApprovedThesisDTO dto = new ApprovedThesisDTO();
        dto.setTitle("AI Thesis");

        when(modelMapper.map(any(), eq(ApprovedThesisDTO.class))).thenReturn(dto);

        ApprovedThesisDTO result = thesisService.approveThesis(1L);

        assertEquals("AI Thesis", result.getTitle());
        verify(thesisRepository).save(any());
    }

    //If Thesis ID doesn't exist throw an Exception
    @Test
    void approveThesisThrowTest() {

        when(thesisRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> thesisService.approveThesis(1L));

    }

}