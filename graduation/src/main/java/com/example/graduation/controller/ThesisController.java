package com.example.graduation.controller;

import com.example.graduation.dto.thesis.*;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.service.ThesisService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/theses")
public class ThesisController {

    private final ThesisService thesisService;


    //Read
    //
    //Submitted Theses
    @GetMapping("/submitted")
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return thesisService.getAllApprovedTheses(title, studentNumber, page, size, sortField, sortDir);
    }


    //Defended Theses
    @GetMapping("/defended")
    public Page<DefendedThesisDTO> getAllDefendedTheses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) Grade grade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return thesisService.getAllDefendedTheses(title, studentNumber, grade, page, size, sortField, sortDir);
    }



    //Create
    //
    //Initial Status - Submitted
    @PostMapping("/submit")
    public ResponseEntity<SubmittedThesisDTO> submitThesis(@Valid @RequestBody SubmittedThesisDTO thesisDTO) {
        SubmittedThesisDTO result = thesisService.submitThesis(thesisDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }



    //Update
    //
    //Submitted Thesis
    @PutMapping("/submitted/{id}")
    public ResponseEntity<SubmittedThesisDTO> updateSubmittedThesis(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubmittedThesisDTO thesisDTO) {

        SubmittedThesisDTO updated = thesisService.updateThesis(id, thesisDTO);
        return ResponseEntity.ok(updated);
    }

    //Defended Thesis
    @PutMapping("/defended/{id}")
    public ResponseEntity<DefendedThesisDTO> updateDefendedThesis(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDefendedThesisDTO thesisDTO) {

        DefendedThesisDTO updated = thesisService.updateDefendedThesis(id, thesisDTO);
        return ResponseEntity.ok(updated);
    }


    //Submitted Thesis -> Approved Thesis
    @PutMapping("/submitted/{id}/approve")
    public ResponseEntity<ApprovedThesisDTO> approveThesis(@PathVariable Long id) {
        ApprovedThesisDTO approvedThesis = thesisService.approveThesis(id);
        return ResponseEntity.ok(approvedThesis);
    }

    //Approved Thesis -> Defended Thesis
    @PutMapping("/approved/{id}/defend")
    public ResponseEntity<DefendedThesisDTO> defendThesis(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDefendedThesisDTO defendedDTO) {

        DefendedThesisDTO defendedThesis = thesisService.defendThesis(id, defendedDTO);
        return ResponseEntity.ok(defendedThesis);
    }



    //Delete
    //
    //Delete Thesis
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThesis(@PathVariable Long id) {
        thesisService.deleteThesis(id);
        return ResponseEntity.noContent().build();
    }


}
