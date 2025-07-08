package com.example.graduation.unit;

import com.example.graduation.dto.teacher.CreateTeacherDTO;
import com.example.graduation.dto.teacher.TeacherDTO;
import com.example.graduation.dto.teacher.UpdateTeacherDTO;
import com.example.graduation.entity.Teacher;
import com.example.graduation.exception.TeacherNotFoundException;
import com.example.graduation.repository.TeacherRepository;
import com.example.graduation.service.TeacherService;
import com.example.graduation.service.UserService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TeacherService teacherService;

    //Test subject initialized in @BeforeEach
    private Long id = 1L;
    private Teacher teacher;


    @BeforeEach
    public void setup() {
        // Create example Teacher entity
        teacher = new Teacher();

        teacher.setId(1L);
        teacher.setFirstName("Mary");
        teacher.setLastName("Smith");
        teacher.setTeacherNumber("T123456");
    }


    //Create Teacher
    @Test
    void createTeacherTest() {
        //Initialize DTO
        CreateTeacherDTO teacherDTO = new CreateTeacherDTO();
        teacherDTO.setFirstName("Mary");
        teacherDTO.setLastName("Smith");
        teacherDTO.setTeacherNumber("T123456");

        //Save to Database
        when(modelMapper.map(teacherDTO, Teacher.class)).thenReturn(teacher);
        when(teacherRepository.save(teacher)).thenReturn(teacher);
        when(modelMapper.map(teacher, CreateTeacherDTO.class)).thenReturn(teacherDTO);

        //Create the Teacher
        CreateTeacherDTO result = teacherService.createTeacher(teacherDTO);

        //Check if DTO has saved correctly
        assertEquals("Mary", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("T123456", result.getTeacherNumber());

        verify(teacherRepository).save(teacher);
    }


    //Read Teachers
    @Test
    void getAllTeachersTest() {

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setFirstName("Mary");
        teacherDTO.setLastName("Smith");
        teacherDTO.setTeacherNumber("T123456");


        Page<Teacher> teacherPage = new PageImpl<>(List.of(teacher));

        when(modelMapper.map(teacher, TeacherDTO.class)).thenReturn(teacherDTO);
        when(teacherRepository.findAll(
                ArgumentMatchers.<Specification<Teacher>>any(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(teacherPage);

        // When
        Page<TeacherDTO> result = teacherService.getAllTeachers(
                "Mary", "Smith", "T123456", 0, 10, "firstName", "asc");

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Mary", result.getContent().get(0).getFirstName());
        assertEquals("Smith", result.getContent().get(0).getLastName());
        assertEquals("T123456", result.getContent().get(0).getTeacherNumber());
    }


    //Update Teacher
    @Test
    void updateTeacherTest() {
        // Given
        UpdateTeacherDTO inputDTO = new UpdateTeacherDTO();
        inputDTO.setFirstName("Updated");
        inputDTO.setLastName("Updated");

        // Reuse 'teacher' created in @BeforeEach as the existing teacher
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Simulate update
        teacher.setFirstName(inputDTO.getFirstName());
        teacher.setLastName(inputDTO.getLastName());

        when(teacherRepository.save(teacher)).thenReturn(teacher);
        when(modelMapper.map(teacher, UpdateTeacherDTO.class)).thenReturn(inputDTO);

        // When
        UpdateTeacherDTO result = teacherService.updateTeacher(1L, inputDTO);

        // Then
        assertEquals("Updated", result.getFirstName());
        assertEquals("Updated", result.getLastName());
    }


    //Delete Teacher
    //
    //If Teacher exists
    @Test
    void deleteTeacherSuccessTest() {

        when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher(id);

        verify(teacherRepository).delete(teacher);
    }

    //If Teacher doesn't exist
    @Test
    void deleteTeacherThrowTest() {

        when(teacherRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TeacherNotFoundException.class, () -> teacherService.deleteTeacher(id));
    }


    //Get Teacher By ID
    @Test
    void getTeacherById_shouldReturnMappedDTO() {

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setFirstName("Mary");
        teacherDTO.setLastName("Smith");
        teacherDTO.setTeacherNumber("T123456");


        when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));
        when(modelMapper.map(teacher, TeacherDTO.class)).thenReturn(teacherDTO);

        Optional<TeacherDTO> result = teacherService.getTeacherById(id);

        assertTrue(result.isPresent());
        assertEquals("Mary", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());
        assertEquals("T123456", result.get().getTeacherNumber());

        verify(teacherRepository).findById(id);
    }
}