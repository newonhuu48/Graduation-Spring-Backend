package com.example.graduation.controller;

import com.example.graduation.dto.student.CreateStudentDTO;
import com.example.graduation.dto.student.StudentDTO;
import com.example.graduation.dto.student.UpdateStudentDTO;
import com.example.graduation.service.StudentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;


    //Read
    @GetMapping
    public Page<StudentDTO> getStudents(
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) String studentNumber,
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortField,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return studentService.getAllStudents(
                firstName, lastName, studentNumber, pageNumber, pageSize, sortField, sortDir);
    }


    //Create
    @PostMapping
    public ResponseEntity<CreateStudentDTO> createStudent(@RequestBody CreateStudentDTO studentDTO) {
        return new ResponseEntity<>(studentService.createStudent(studentDTO), HttpStatus.CREATED);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<UpdateStudentDTO> updateStudent(@PathVariable Long id, @RequestBody UpdateStudentDTO studentDTO) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }


}
