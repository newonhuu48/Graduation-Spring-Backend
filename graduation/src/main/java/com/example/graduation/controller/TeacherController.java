package com.example.graduation.controller;

import com.example.graduation.dto.teacher.CreateTeacherDTO;
import com.example.graduation.dto.teacher.TeacherDTO;
import com.example.graduation.dto.teacher.UpdateTeacherDTO;
import com.example.graduation.service.TeacherService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/teachers")
public class TeacherController {

    private final ModelMapper modelMapper;
    private final TeacherService teacherService;


    //Read
    @GetMapping
    public Page<TeacherDTO> getTeachers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String teacherNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return teacherService.getAllTeachers(firstName, lastName, teacherNumber, page, size, sortField, sortDir);
    }



    //Create
    @PostMapping
    public ResponseEntity<CreateTeacherDTO> createTeacher(@RequestBody CreateTeacherDTO teacherDTO) {
        return new ResponseEntity<>(teacherService.createTeacher(teacherDTO), HttpStatus.CREATED);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<UpdateTeacherDTO> updateTeacher(@PathVariable Long id, @RequestBody UpdateTeacherDTO teacherDTO) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacherDTO));
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }


}
