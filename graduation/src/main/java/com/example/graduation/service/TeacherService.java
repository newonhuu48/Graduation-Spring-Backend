package com.example.graduation.service;

import com.example.graduation.dto.teacher.CreateTeacherDTO;
import com.example.graduation.dto.teacher.TeacherDTO;
import com.example.graduation.dto.teacher.UpdateTeacherDTO;
import com.example.graduation.entity.Teacher;
import com.example.graduation.entity.User;
import com.example.graduation.exception.TeacherNotFoundException;
import com.example.graduation.repository.TeacherRepository;
import com.example.graduation.repository.UserRepository;
import com.example.graduation.repository.specification.TeacherSpecification;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;


    public Page<TeacherDTO> getAllTeachers(
            String firstName, String lastName, String teacherNumber,
            int page, int size, String sortField, String sortDir) {


        Pageable pageable = PageRequest.of(page, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());


        //Initialize Specification
        Specification<Teacher> spec = (root, query, cb) -> cb.conjunction();


        //Criterial queries - First Name, Last Name and Teacher Number
        if(firstName != null && !firstName.isBlank() ) {
            spec = spec.and(TeacherSpecification.firstNameContains(firstName));
        }
        if(lastName != null && !lastName.isBlank() ) {
            spec = spec.and(TeacherSpecification.lastNameContains(lastName));
        }
        if(teacherNumber != null && !teacherNumber.isBlank()) {
            spec = spec.and(TeacherSpecification.teacherNumberContains(teacherNumber));
        }


        Page<Teacher> teachersPage = teacherRepository.findAll(spec, pageable);

        return teachersPage.map(this::convertToTeacherDTO);
    }



    //CREATE UPDATE DELETE Student
    //
    //
    //CREATE Student
    public CreateTeacherDTO createTeacher(CreateTeacherDTO teacherDTO) {

        Teacher teacher = convertToEntity(teacherDTO);
        Teacher savedTeacher = teacherRepository.save(teacher);

        return convertToCreateTeacherDTO(savedTeacher);
    }


    //UPDATE Teacher
    public UpdateTeacherDTO updateTeacher(long id, UpdateTeacherDTO teacherDTO) {

        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));


        existingTeacher.setFirstName(teacherDTO.getFirstName());
        existingTeacher.setLastName(teacherDTO.getLastName());


        Teacher savedTeacher = teacherRepository.save(existingTeacher);

        return convertToUpdateTeacherDTO(savedTeacher);
    }


    //DELETE Teacher
    public void deleteTeacher(long id) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));

        teacherRepository.delete(teacher);
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
    public TeacherDTO getTeacherById(long id) {
        return teacherRepository.findById(id)
                .map(this::convertToTeacherDTO)
                .orElseThrow(() -> new TeacherNotFoundException(id));
    }

    //
    //
    //ENTITY -> DTO
    private TeacherDTO convertToTeacherDTO(Teacher teacher) {
        return modelMapper.map(teacher, TeacherDTO.class);
    }

    private CreateTeacherDTO convertToCreateTeacherDTO(Teacher teacher) {
        return modelMapper.map(teacher, CreateTeacherDTO.class);
    }

    private UpdateTeacherDTO convertToUpdateTeacherDTO(Teacher teacher) {
        return modelMapper.map(teacher, UpdateTeacherDTO.class);
    }


    //
    //
    //DTO -> ENTITY
    private Teacher convertToEntity(TeacherDTO teacherDto) {
        return modelMapper.map(teacherDto, Teacher.class);
    }

    private Teacher convertToEntity(CreateTeacherDTO teacherDto) {
        return modelMapper.map(teacherDto, Teacher.class);
    }

    private Teacher convertToEntity(UpdateTeacherDTO teacherDto) {
        return modelMapper.map(teacherDto, Teacher.class);
    }
}