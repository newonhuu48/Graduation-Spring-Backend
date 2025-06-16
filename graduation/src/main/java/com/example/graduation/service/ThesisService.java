package com.example.graduation.service;

import com.example.graduation.dto.thesis.*;
import com.example.graduation.entity.Student;
import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.User;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.entity.enums.ThesisStatus;
import com.example.graduation.exception.StudentNotFoundException;
import com.example.graduation.exception.ThesisNotFoundException;
import com.example.graduation.repository.ThesisRepository;
import com.example.graduation.repository.UserRepository;
import com.example.graduation.repository.specification.ThesisSpecification;
import jakarta.persistence.EntityNotFoundException;
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
public class ThesisService {

    private final ThesisRepository thesisRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;



    //Theses by Status - SUBMITTED
    //
    //Get all Submitted Theses
    public Page<SubmittedThesisDTO> getAllSubmittedTheses(
            String title, String studentNumber,
            int page, int size, String sortField, String sortDir
    ) {

        Pageable pageable = PageRequest.of(page, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());


        //Initialize Specification
        Specification<Thesis> spec = (root, query, cb) -> cb.conjunction();

        //Fetch only SUBMITTED Theses
        ThesisStatus desiredStatus = ThesisStatus.SUBMITTED;
        spec = spec.and(ThesisSpecification.statusEquals(desiredStatus));


        //Criterial queries - Title and Student Number
        if(title != null && !title.isBlank() ) {
            spec = spec.and(ThesisSpecification.titleContains(title));
        }
        if(studentNumber != null && !studentNumber.isBlank()) {
            spec = spec.and(ThesisSpecification.studentNumberEquals(studentNumber));
        }


        Page<Thesis> thesesPage = thesisRepository.findAll(spec, pageable);

        return thesesPage.map(this::convertToSubmittedThesisDTO);
    }

    //Add thesis - Initial Status SUBMITTED
    public SubmittedThesisDTO submitThesis(SubmittedThesisDTO thesisDTO) {

        Thesis thesis = convertToEntity(thesisDTO);

        //Initial status of thesis - Submitted
        thesis.setStatus(ThesisStatus.SUBMITTED);


        Thesis savedThesis = thesisRepository.save(thesis);
        return convertToSubmittedThesisDTO(savedThesis);
    }


    //Update Submitted Thesis
    public SubmittedThesisDTO updateThesis(Long id, UpdateSubmittedThesisDTO thesisDTO) {

        Thesis existingThesis = thesisRepository.findById(id)
                .orElseThrow(() -> new ThesisNotFoundException(id));

        existingThesis.setTitle(thesisDTO.getTitle());


        Thesis savedThesis = thesisRepository.save(existingThesis);

        return convertToSubmittedThesisDTO(savedThesis);
    }


    //Change Thesis Status from Submitted to Approved
    public ApprovedThesisDTO approveThesis(Long id) {

        Thesis thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thesis not found"));

        //Approve thesis
        thesis.setStatus(ThesisStatus.APPROVED);
        Thesis savedThesis = thesisRepository.save(thesis);

        return convertToApprovedThesisDTO(savedThesis);
    }





    //Theses by Status - APPROVED
    //
    //Get all Approved Theses
    public Page<ApprovedThesisDTO> getAllApprovedTheses(
            String title, String studentNumber,
            int page, int size, String sortField, String sortDir
    ) {

        Pageable pageable = PageRequest.of(page, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());


        //Initialize Specification
        Specification<Thesis> spec = (root, query, cb) -> cb.conjunction();

        //Fetch only APPROVED Theses
        ThesisStatus desiredStatus = ThesisStatus.APPROVED;
        spec = spec.and(ThesisSpecification.statusEquals(desiredStatus));


        //Criterial queries - Title and Student Number
        if(title != null && !title.isBlank() ) {
            spec = spec.and(ThesisSpecification.titleContains(title));
        }
        if(studentNumber != null && !studentNumber.isBlank()) {
            spec = spec.and(ThesisSpecification.studentNumberEquals(studentNumber));
        }


        Page<Thesis> thesesPage = thesisRepository.findAll(spec, pageable);


        return thesesPage.map(this::convertToApprovedThesisDTO);
    }




    //Theses by Status - DEFENDED
    //
    //Get all Defended Theses
    public Page<DefendedThesisDTO> getAllDefendedTheses(
            String title, String studentNumber, Grade grade,
            int page, int size, String sortField, String sortDir
    ) {


        Pageable pageable = PageRequest.of(page, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());


        //Initialize Specification
        Specification<Thesis> spec = (root, query, cb) -> cb.conjunction();

        //Fetch only DEFENDED Theses
        ThesisStatus desiredStatus = ThesisStatus.DEFENDED;
        spec = spec.and(ThesisSpecification.statusEquals(desiredStatus));


        //Criterial queries - Title, Student Number and Grade
        if(title != null && !title.isBlank() ) {
            spec = spec.and(ThesisSpecification.titleContains(title));
        }
        if(studentNumber != null && !studentNumber.isBlank()) {
            spec = spec.and(ThesisSpecification.studentNumberEquals(studentNumber));
        }
        if(grade != null) {
            spec = spec.and(ThesisSpecification.gradeEquals(grade));
        }


        Page<Thesis> thesesPage = thesisRepository.findAll(spec, pageable);


        return thesesPage.map(this::convertToDefendedThesisDTO);
    }

    //Update Defended Thesis
    public DefendedThesisDTO updateDefendedThesis(Long id, UpdateDefendedThesisDTO thesisDTO) {

        Thesis thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thesis not found"));

        thesis.setGrade(thesisDTO.getGrade());


        Thesis savedThesis = thesisRepository.save(thesis);

        return convertToDefendedThesisDTO(savedThesis);
    }


    //Change thesis status from Approved to Defended
    public DefendedThesisDTO defendThesis(Long id, UpdateDefendedThesisDTO thesisDTO) {

        Thesis thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thesis not found"));


        //Change thesis status
        thesis.setStatus(ThesisStatus.DEFENDED);
        thesis.setGrade(thesisDTO.getGrade());


        Thesis savedThesis = thesisRepository.save(thesis);

        return modelMapper.map(savedThesis, DefendedThesisDTO.class);
    }




    //Theses by Action - DELETE
    //
    //One method for Submitted, Approved and Defended Theses
    public void deleteThesis(Long id) {

        Thesis thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thesis not found"));

        thesisRepository.delete(thesis);
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
    //
    //ENTITY -> DTO
    //
    //Entity -> Submitted Thesis DTO
    private SubmittedThesisDTO convertToSubmittedThesisDTO(Thesis thesis) {
        SubmittedThesisDTO dto = modelMapper.map(thesis, SubmittedThesisDTO.class);

        if (thesis.getStudent() != null) {
            dto.setStudentId(thesis.getStudent().getId());
            dto.setStudentNumber(thesis.getStudent().getStudentNumber());
        }

        return dto;
    }

    //Entity -> Approved Thesis DTO
    private ApprovedThesisDTO convertToApprovedThesisDTO(Thesis thesis) {
        ApprovedThesisDTO dto = modelMapper.map(thesis, ApprovedThesisDTO.class);

        if (thesis.getStudent() != null) {
            dto.setStudentId(thesis.getStudent().getId());
            dto.setStudentNumber(thesis.getStudent().getStudentNumber());
        }

        return dto;
    }


    //Entity -> Defended Thesis DTO
    private DefendedThesisDTO convertToDefendedThesisDTO(Thesis thesis) {
        DefendedThesisDTO dto = modelMapper.map(thesis, DefendedThesisDTO.class);

        if (thesis.getStudent() != null) {
            dto.setStudentId(thesis.getStudent().getId());
            dto.setStudentNumber(thesis.getStudent().getStudentNumber());

            dto.setGrade(thesis.getGrade());
        }

        return dto;
    }


    //
    //
    //DTO -> ENTITY
    //
    //
    private Thesis convertToEntity(SubmittedThesisDTO thesisDTO) {return modelMapper.map(thesisDTO, Thesis.class);}

    private Thesis convertToEntity(ApprovedThesisDTO thesisDTO) {return modelMapper.map(thesisDTO, Thesis.class);}

    private Thesis convertToEntity(DefendedThesisDTO thesisDTO) {return modelMapper.map(thesisDTO, Thesis.class);}


}
