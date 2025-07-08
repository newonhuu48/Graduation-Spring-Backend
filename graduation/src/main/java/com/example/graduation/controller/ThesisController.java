package com.example.graduation.controller;

import com.example.graduation.dto.thesis.*;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.service.ThesisService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;


@RestController
@AllArgsConstructor
@RequestMapping("/api/theses")
public class ThesisController {

    private final ThesisService thesisService;


    //Student View Methods
    //
    //Create
    @PostMapping("/student/submit")
    //Student View
    //Only Student can submit Thesis from this view
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmittedThesisDTO> submitThesisByStudent(@Valid @RequestBody CreateSubmittedThesisDTO createDTO) throws AccessDeniedException {

        SubmittedThesisDTO result = thesisService.submitThesisByStudent(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    //Get Thesis
    @GetMapping("/student/my-thesis")
    //Student View
    //Only Student can see their own thesis
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentThesisDTO> getOwnThesis() {
        return thesisService.getOwnThesis()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




    //Read
    //
    //Submitted Theses
    @GetMapping("/submitted")
    //Only ROLE_TEACHER can see Submitted Theses
    @PreAuthorize("hasRole('TEACHER')")
    public Page<SubmittedThesisDTO> getAllSubmittedTheses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return thesisService.getAllSubmittedTheses(
                title, studentNumber, pageNumber, pageSize, sortField, sortDir);
    }


    //Approved Theses
    @GetMapping("/approved")
    public Page<ApprovedThesisDTO> getAllApprovedTheses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return thesisService.getAllApprovedTheses(
                title, studentNumber, pageNumber, pageSize, sortField, sortDir);
    }


    //Defended Theses
    @GetMapping("/defended")
    //Only ROLE_TEACHER can see Defended Theses
    @PreAuthorize("hasRole('TEACHER')")
    public Page<DefendedThesisDTO> getAllDefendedTheses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) Grade grade,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return thesisService.getAllDefendedTheses(
                title, studentNumber, grade, pageNumber, pageSize, sortField, sortDir);
    }


    //Get Submitted Thesis By ID - To show on Edit Form
    @GetMapping("/submitted/{id}")
    public ResponseEntity<UpdateSubmittedThesisDTO> getSubmittedThesisById(@PathVariable Long id) {
        UpdateSubmittedThesisDTO thesisDTO = thesisService.getSubmittedThesisById(id);
        return ResponseEntity.ok(thesisDTO);
    }

    //Get Defended Thesis By ID - To Show on Edit Form
    @GetMapping("/defended/{id}")
    //Only ROLE_TEACHER can open Defended Thesis Edit Form
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<UpdateDefendedThesisDTO> getDefendedThesisById(@PathVariable Long id) {
        UpdateDefendedThesisDTO thesisDTO = thesisService.getDefendedThesisById(id);
        return ResponseEntity.ok(thesisDTO);
    }

    //Create
    //
    //Initial Status - Submitted
    @PostMapping("/submit")
    public ResponseEntity<SubmittedThesisDTO> submitThesis(@Valid @RequestBody CreateSubmittedThesisDTO createDTO) {

        SubmittedThesisDTO result = thesisService.submitThesis(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }



    //Update
    //
    //Submitted Thesis
    @PutMapping("/submitted/{id}")
    public ResponseEntity<SubmittedThesisDTO> updateSubmittedThesis(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubmittedThesisDTO thesisDTO) {

        SubmittedThesisDTO updated = thesisService.updateSubmittedThesis(id, thesisDTO);
        return ResponseEntity.ok(updated);
    }

    //Defended Thesis
    @PutMapping("/defended/{id}")
    //Only ROLE_TEACHER can Edit Defended Theses
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<DefendedThesisDTO> updateDefendedThesis(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDefendedThesisDTO thesisDTO) {

        DefendedThesisDTO updated = thesisService.updateDefendedThesis(id, thesisDTO);
        return ResponseEntity.ok(updated);
    }


    //Change Thesis status
    //
    //Submitted Thesis -> Approved Thesis
    @PutMapping("/submitted/{id}/approve")
    //Only ROLE_TEACHER can Approve Submitted Theses
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApprovedThesisDTO> approveThesis(@PathVariable Long id) {
        ApprovedThesisDTO approvedThesis = thesisService.approveThesis(id);
        return ResponseEntity.ok(approvedThesis);
    }

    //Approved Thesis -> Defended Thesis
    //Only ROLE_TEACHER can change Thesis status to Defended
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/approved/{id}/defend")
    public ResponseEntity<CreateDefendedThesisDTO> defendThesis(
            @PathVariable Long id,
            @Valid @RequestBody CreateDefendedThesisDTO defendedDTO) {

        CreateDefendedThesisDTO defendedThesis = thesisService.defendThesis(id, defendedDTO);

        return ResponseEntity.ok(defendedThesis);
    }



    //Delete
    //
    //Delete Thesis
    @DeleteMapping("/{id}")
    //Only ROLE_TEACHER can delete Theses
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteThesis(@PathVariable Long id) {
        thesisService.deleteThesis(id);
        return ResponseEntity.noContent().build();
    }


}
