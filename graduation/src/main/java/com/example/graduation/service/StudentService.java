package com.example.graduation.service;

import com.example.graduation.dto.student.CreateStudentDTO;
import com.example.graduation.dto.student.StudentDTO;
import com.example.graduation.dto.student.UpdateStudentDTO;
import com.example.graduation.entity.User;
import com.example.graduation.exception.StudentNotFoundException;
import com.example.graduation.repository.StudentRepository;
import com.example.graduation.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import com.example.graduation.entity.Student;
import com.example.graduation.repository.specification.StudentSpecification;

import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserService userService;

    private final ModelMapper modelMapper;



    public Page<StudentDTO> getAllStudents(
            String firstName, String lastName, String studentNumber,
            int page, int size, String sortField, String sortDir) {


        Pageable pageable = PageRequest.of(page, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());


        //Initialize Specification
        Specification<Student> spec = (root, query, cb) -> cb.conjunction();


        //Criterial queries - First Name, Last Name and Student Number
        if(firstName != null && !firstName.isBlank() ) {
            spec = spec.and(StudentSpecification.firstNameContains(firstName));
        }
        if(lastName != null && !lastName.isBlank() ) {
            spec = spec.and(StudentSpecification.lastNameContains(lastName));
        }
        if(studentNumber != null && !studentNumber.isBlank()) {
            spec = spec.and(StudentSpecification.studentNumberEquals(studentNumber));
        }


        Page<Student> studentsPage = studentRepository.findAll(spec, pageable);

        return studentsPage.map(this::convertToStudentDTO);
    }


    //CREATE UPDATE DELETE Student
    //
    //
    //CREATE Student
    public CreateStudentDTO createStudent(CreateStudentDTO studentDTO) {

        Student student = convertToEntity(studentDTO);
        Student savedStudent = studentRepository.save(student);

        return convertToCreateStudentDTO(savedStudent);
    }


    //UPDATE Student
    public UpdateStudentDTO updateStudent(long id, UpdateStudentDTO studentDTO) {

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));


        existingStudent.setFirstName(studentDTO.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName());


        Student savedStudent = studentRepository.save(existingStudent);

        return convertToUpdateStudentDTO(savedStudent);
    }


    //DELETE Student
    public void deleteStudent(long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        studentRepository.delete(student);
    }



    //HELPER FUNCTIONS
    //
    //Get Student By ID
    public Optional<StudentDTO> getStudentById(long id) {
        return studentRepository.findById(id)
                .map(this::convertToStudentDTO);
    }

    //To load DTO on Edit Form
    public Optional<UpdateStudentDTO> getEditStudentById(long id) {
        return studentRepository.findById(id)
                .map(this::convertToUpdateStudentDTO);
    }



    //
    //
    //ENTITY -> DTO
    private StudentDTO convertToStudentDTO(Student student) {
        return modelMapper.map(student, StudentDTO.class);
    }

    private CreateStudentDTO convertToCreateStudentDTO(Student student) {
        return modelMapper.map(student, CreateStudentDTO.class);
    }

    private UpdateStudentDTO convertToUpdateStudentDTO(Student student) {
        return modelMapper.map(student, UpdateStudentDTO.class);
    }


    //
    //
    //DTO -> ENTITY
    private Student convertToEntity(StudentDTO studentDto) {
        return modelMapper.map(studentDto, Student.class);
    }

    private Student convertToEntity(CreateStudentDTO studentDto) {
        return modelMapper.map(studentDto, Student.class);
    }

    private Student convertToEntity(UpdateStudentDTO studentDto) {
        return modelMapper.map(studentDto, Student.class);
    }
}
