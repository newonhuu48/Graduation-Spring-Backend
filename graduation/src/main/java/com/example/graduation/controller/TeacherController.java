package com.example.graduation.controller;

import com.example.graduation.dto.teacher.CreateTeacherDTO;
import com.example.graduation.dto.teacher.TeacherDTO;
import com.example.graduation.dto.teacher.UpdateTeacherDTO;
import com.example.graduation.service.TeacherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/teachers")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;


    //Read
    @GetMapping
    public Page<TeacherDTO> getTeachers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String teacherNumber,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return teacherService.getAllTeachers(
                firstName, lastName, teacherNumber, pageNumber, pageSize, sortField, sortDir);
    }

    //Get Teacher By ID
    @GetMapping("{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Fetch UpdateTeacherDTO to fill Edit Form
    @GetMapping("{id}/edit")
    public ResponseEntity<UpdateTeacherDTO> getTeacherForEdit(@PathVariable Long id) {
        return teacherService.getEditTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //Create
    @PostMapping
    public ResponseEntity<CreateTeacherDTO> createTeacher(@Valid @RequestBody CreateTeacherDTO teacherDTO) {
        return new ResponseEntity<>(teacherService.createTeacher(teacherDTO), HttpStatus.CREATED);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<UpdateTeacherDTO> updateTeacher(@Valid @PathVariable Long id, @RequestBody UpdateTeacherDTO teacherDTO) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacherDTO));
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }


}
