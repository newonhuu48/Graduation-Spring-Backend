package com.example.graduation.service;

import com.example.graduation.dto.student.CreateStudentDTO;
import com.example.graduation.dto.student.StudentDTO;
import com.example.graduation.dto.student.UpdateStudentDTO;
import com.example.graduation.entity.User;
import com.example.graduation.exception.StudentNotFoundException;
import com.example.graduation.repository.StudentRepository;
import com.example.graduation.repository.UserRepository;
import lombok.AllArgsConstructor;
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

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;



    public Page<StudentDTO> getAllStudents(
            String firstName, String lastName, String studentNumber,
            int page, int size, String sortField, String sortDir) {


        Pageable pageable = PageRequest.of(page, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());


        Specification<Student> spec = (root, query, cb) -> cb.conjunction();


        //ADDING FIELDS TO SPECIFICATION
        //FOR CRITERIA QUERY
        if(firstName != null && !firstName.isBlank() ) {
            spec = spec.and(StudentSpecification.firstNameContains(firstName));
        }
        if(lastName != null && !lastName.isBlank() ) {
            spec = spec.and(StudentSpecification.lastNameContains(lastName));
        }
        if(studentNumber != null && !studentNumber.isBlank()) {
            spec = spec.and(StudentSpecification.studentNumberContains(studentNumber));
        }


        Page<Student> studentsPage = studentRepository.findAll(spec, pageable);

        return studentsPage.map(student -> modelMapper.map(student, StudentDTO.class));
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
    //Get Currently Logged In USER (for Role-Based Access Control
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    //
    //Get Student By ID
    public StudentDTO getStudentById(long id) {
        return studentRepository.findById(id)
                .map(this::convertToStudentDTO)
                .orElseThrow(() -> new StudentNotFoundException(id));
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
