package com.example.graduation.unit.service;

import com.example.graduation.dto.thesis.CreateSubmittedThesisDTO;
import com.example.graduation.dto.thesis.StudentThesisDTO;
import com.example.graduation.dto.thesis.SubmittedThesisDTO;
import com.example.graduation.entity.Student;
import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.User;
import com.example.graduation.entity.enums.ThesisStatus;
import com.example.graduation.repository.StudentRepository;
import com.example.graduation.repository.ThesisRepository;
import com.example.graduation.service.ThesisService;
import com.example.graduation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThesisServiceStudentViewTest {
    @Mock
    private ThesisRepository thesisRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ThesisService thesisService;


    private Long id = 1L;
    private Student student;
    private Thesis thesis;
    private User currentUser;


    @BeforeEach
    void setup() {
        // Simple test data
        student = new Student();
        student.setId(1L);
        student.setStudentNumber("F123456");

        thesis = new Thesis();
        thesis.setTitle("My Thesis");
        thesis.setStudent(student);
        thesis.setStatus(ThesisStatus.SUBMITTED);

        // Mock current user with student_owner
        currentUser = mock(User.class);
        when(currentUser.getStudent_owner()).thenReturn(student);

        // Mock user service
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.isUserStudent(currentUser)).thenReturn(true);
    }



    //Student submits Thesis by themselves
    @Test
    void submitThesisByStudentTest() throws AccessDeniedException {
        // Arrange
        CreateSubmittedThesisDTO createThesisDTO = new CreateSubmittedThesisDTO();
        createThesisDTO.setTitle("My Thesis");
        createThesisDTO.setStatus(ThesisStatus.SUBMITTED); // or leave unset, since service sets it anyway
        createThesisDTO.setStudentId(student.getId()); // if your convertToEntity uses it

        when(thesisRepository.save(Mockito.<Thesis>any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        when(modelMapper.map(any(Thesis.class), eq(SubmittedThesisDTO.class))).thenAnswer(invocation -> {
            Thesis arg = invocation.getArgument(0);

            SubmittedThesisDTO submittedThesisDTO = new SubmittedThesisDTO();

            if (arg.getStudent() != null) {
                submittedThesisDTO.setStudentId(arg.getStudent().getId());
                submittedThesisDTO.setStudentNumber(arg.getStudent().getStudentNumber());
            }

            submittedThesisDTO.setTitle(arg.getTitle());
            submittedThesisDTO.setStatus(arg.getStatus());

            return submittedThesisDTO;
        });


        // Act
        SubmittedThesisDTO result = thesisService.submitThesisByStudent(createThesisDTO);
        //I probably have to do something with Student Repository

        // Assert
        assertNotNull(result);
        assertEquals(student.getId(), result.getStudentId());
        assertEquals(student.getStudentNumber(), result.getStudentNumber());
        assertEquals(createThesisDTO.getTitle(), result.getTitle());
        assertEquals(ThesisStatus.SUBMITTED, result.getStatus());

        verify(thesisRepository).save(Mockito.<Thesis>any());
    }



    //Student views their own Thesis
    @Test
    void getOwnThesisTest() {
        // Arrange
        StudentThesisDTO expectedThesisDTO = new StudentThesisDTO();
        expectedThesisDTO.setTitle(thesis.getTitle());

        when(thesisRepository.findByStudentId(student.getId())).thenReturn(Optional.of(thesis));
        when(modelMapper.map(thesis, StudentThesisDTO.class)).thenReturn(expectedThesisDTO);

        // Act
        Optional<StudentThesisDTO> result = thesisService.getOwnThesis();

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedThesisDTO, result.get());
    }
}
