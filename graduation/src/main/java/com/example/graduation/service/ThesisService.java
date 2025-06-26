package com.example.graduation.service;

import com.example.graduation.dto.student.StudentDTO;
import com.example.graduation.dto.thesis.*;
import com.example.graduation.entity.Student;
import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.User;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.entity.enums.ThesisStatus;
import com.example.graduation.exception.StudentNotFoundException;
import com.example.graduation.exception.ThesisNotFoundException;
import com.example.graduation.repository.StudentRepository;
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

import java.util.Optional;

@Service
@AllArgsConstructor
public class ThesisService {

    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;

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
    public SubmittedThesisDTO submitThesis(CreateSubmittedThesisDTO createDTO) {

        Thesis thesis = convertToEntity(createDTO);
        thesis.setStatus(ThesisStatus.SUBMITTED);

        Thesis savedThesis = thesisRepository.save(thesis);

        return convertToSubmittedThesisDTO(savedThesis);
    }



    //Update Submitted Thesis
    public SubmittedThesisDTO updateSubmittedThesis(Long id, UpdateSubmittedThesisDTO thesisDTO) {

        Thesis existingThesis = thesisRepository.findById(id)
                .orElseThrow(() -> new ThesisNotFoundException(id));

        existingThesis.setTitle(thesisDTO.getTitle());


        Thesis savedThesis = thesisRepository.save(existingThesis);

        return convertToSubmittedThesisDTO(savedThesis);
    }

    //Get Thesis By ID
    //
    //Get Submitted Thesis By ID - To show on Edit Form
    public UpdateSubmittedThesisDTO getSubmittedThesisById(Long id) {
        Thesis thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thesis not found"));

        if (thesis.getStatus() != ThesisStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted theses can be edited");
        }

        return convertToUpdateSubmittedThesisDTO(thesis);
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


    //Get Defended Thesis By ID - To show on Edit Form
    public UpdateDefendedThesisDTO getDefendedThesisById(Long id) {
        Thesis thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thesis not found"));

        if (thesis.getStatus() != ThesisStatus.DEFENDED) {
            throw new IllegalStateException("Only defended theses can be edited");
        }

        return convertToUpdateDefendedThesisDTO(thesis);
    }


    //Change thesis status from Approved to Defended
    public CreateDefendedThesisDTO defendThesis(Long id, CreateDefendedThesisDTO thesisDTO) {

        Thesis thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thesis not found"));


        //Change thesis status
        thesis.setStatus(ThesisStatus.DEFENDED);
        thesis.setGrade(thesisDTO.getGrade());


        Thesis savedThesis = thesisRepository.save(thesis);

        return modelMapper.map(savedThesis, CreateDefendedThesisDTO.class);
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

    //Get Submitted Thesis By ID
    //To show on Edit Form



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

    private UpdateSubmittedThesisDTO convertToUpdateSubmittedThesisDTO(Thesis thesis) {
        UpdateSubmittedThesisDTO dto = modelMapper.map(thesis, UpdateSubmittedThesisDTO.class);

        return dto;
    }

    private UpdateDefendedThesisDTO convertToUpdateDefendedThesisDTO(Thesis thesis) {
        UpdateDefendedThesisDTO dto = modelMapper.map(thesis, UpdateDefendedThesisDTO.class);

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

    //CreateSubmittedThesisDTO -> Entity
    private Thesis convertToEntity(CreateSubmittedThesisDTO thesisDTO) {
        Thesis thesis = new Thesis();
        thesis.setTitle(thesisDTO.getTitle());
        thesis.setStatus(thesisDTO.getStatus());

        if (thesisDTO.getStudentId() != null) {
            Student student = studentRepository.findById(thesisDTO.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            thesis.setStudent(student);
        }

        return thesis;
    }

    //ApprovedThesisDTO -> Entity
    private Thesis convertToEntity(ApprovedThesisDTO dto) {
        Thesis thesis = new Thesis();
        thesis.setId(dto.getId());
        thesis.setTitle(dto.getTitle());
        thesis.setStatus(ThesisStatus.APPROVED); // if applicable

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            thesis.setStudent(student);
        }

        return thesis;
    }

    //DefendedThesisDTO -> Entity
    private Thesis convertToEntity(DefendedThesisDTO dto) {
        Thesis thesis = new Thesis();

        // 1. Preserve the existing ID so Hibernate knows this is an update
        thesis.setId(dto.getId());

        // 2. Copy simple fields
        thesis.setTitle(dto.getTitle());
        thesis.setStatus(ThesisStatus.DEFENDED);
        thesis.setGrade(dto.getGrade());

        // 3. Manually load and attach the Student entity
        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found: " + dto.getStudentId()));
            thesis.setStudent(student);
        }

        return thesis;
    }


}
