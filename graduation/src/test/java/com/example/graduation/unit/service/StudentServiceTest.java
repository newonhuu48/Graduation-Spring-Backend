package com.example.graduation.unit.service;

import com.example.graduation.dto.student.CreateStudentDTO;
import com.example.graduation.dto.student.StudentDTO;
import com.example.graduation.dto.student.UpdateStudentDTO;
import com.example.graduation.entity.Student;
import com.example.graduation.exception.StudentNotFoundException;
import com.example.graduation.repository.StudentRepository;
import com.example.graduation.service.StudentService;
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
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StudentService studentService;

    //Test subject initialized in @BeforeEach
    private Long id = 1L;
    private Student student;


    @BeforeEach
    public void setup() {
        // Create example Student entity
        student = new Student();

        student.setId(1L);
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setStudentNumber("F123456");
    }


    //Create Student
    @Test
    void createStudentTest() {
        //Initialize DTO
        CreateStudentDTO studentDTO = new CreateStudentDTO();
        studentDTO.setFirstName("Alice");
        studentDTO.setLastName("Smith");
        studentDTO.setStudentNumber("F123456");

        //Save to Database
        when(modelMapper.map(studentDTO, Student.class)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(modelMapper.map(student, CreateStudentDTO.class)).thenReturn(studentDTO);

        //Create the Student
        CreateStudentDTO result = studentService.createStudent(studentDTO);

        //Check if DTO has saved correctly
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("F123456", result.getStudentNumber());

        verify(studentRepository).save(student);
    }


    //Read Students
    @Test
    void getAllStudentsTest() {

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName("Alice");
        studentDTO.setLastName("Smith");
        studentDTO.setStudentNumber("F123456");


        Page<Student> studentPage = new PageImpl<>(List.of(student));


        when(modelMapper.map(student, StudentDTO.class)).thenReturn(studentDTO);
        when(studentRepository.findAll(
                ArgumentMatchers.<Specification<Student>>any(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(studentPage);

        // When
        Page<StudentDTO> result = studentService.getAllStudents(
                "Alice", "Smith", "F123456", 0, 10, "firstName", "asc");

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Alice", result.getContent().get(0).getFirstName());
        assertEquals("Smith", result.getContent().get(0).getLastName());
        assertEquals("F123456", result.getContent().get(0).getStudentNumber());
    }


    //Update Student
    @Test
    void updateStudentTest() {
        // Given
        UpdateStudentDTO inputDTO = new UpdateStudentDTO();
        inputDTO.setFirstName("Updated");
        inputDTO.setLastName("Updated");

        // Reuse 'student' created in @BeforeEach as the existing student
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Simulate update
        student.setFirstName(inputDTO.getFirstName());
        student.setLastName(inputDTO.getLastName());

        when(studentRepository.save(student)).thenReturn(student);
        when(modelMapper.map(student, UpdateStudentDTO.class)).thenReturn(inputDTO);

        // When
        UpdateStudentDTO result = studentService.updateStudent(1L, inputDTO);

        // Then
        assertEquals("Updated", result.getFirstName());
        assertEquals("Updated", result.getLastName());
    }


    //Delete Student
    //
    //If Student exists
    @Test
    void deleteStudentSuccessTest() {

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        studentService.deleteStudent(id);

        verify(studentRepository).delete(student);
    }

    //If Student doesn't exist
    @Test
    void deleteStudentThrowTest() {

        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(id));
    }


    //Get Student By ID
    @Test
    void getStudentByIdTest() {

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName("Alice");
        studentDTO.setLastName("Smith");
        studentDTO.setStudentNumber("F123456");


        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(modelMapper.map(student, StudentDTO.class)).thenReturn(studentDTO);

        Optional<StudentDTO> result = studentService.getStudentById(id);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());
        assertEquals("F123456", result.get().getStudentNumber());

        verify(studentRepository).findById(id);




    }


}