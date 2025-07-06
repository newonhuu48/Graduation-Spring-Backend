package com.example.graduation.service;

import com.example.graduation.dto.thesis.*;
import com.example.graduation.entity.Student;
import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.User;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.entity.enums.ThesisStatus;
import com.example.graduation.exception.ThesisNotFoundException;
import com.example.graduation.repository.StudentRepository;
import com.example.graduation.repository.ThesisRepository;
import com.example.graduation.repository.specification.ThesisSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ThesisService {

    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;


    //Student View
    //
    //Upload thesis - Initial Status SUBMITTED
    public SubmittedThesisDTO submitThesisByStudent(CreateSubmittedThesisDTO thesisDTO) throws AccessDeniedException {

        User currentUser = userService.getCurrentUser();


        //Additional Role Check aside from @PreAuthorize("hasRole('STUDENT')")
        if ( !userService.isUserStudent(currentUser) ) {
            throw new AccessDeniedException("Only students can upload theses.");
        }


        Thesis thesis = convertToEntity(thesisDTO, currentUser);

        thesis.setStudent( currentUser.getStudent_owner() );
        thesis.setStatus(ThesisStatus.SUBMITTED);

        thesisRepository.save(thesis);

        return convertToSubmittedThesisDTO(thesis);
    }

    //Student View
    //
    //Get Thesis By Student - Currently logged-in user
    public Optional<StudentThesisDTO> getOwnThesis() {
        User currentUser = userService.getCurrentUser();

        if (!userService.isUserStudent(currentUser)) {
            throw new RuntimeException("User is not a student");
        }

        Long studentId = currentUser.getStudent_owner().getId();

        //Debug
        System.out.println("Student ID: " + studentId);


        return thesisRepository.findByStudentId(studentId)
                .map(this::convertToStudentThesisDTO);
    }





    //Teacher View
    //
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


    //Upload thesis - Initial Status SUBMITTED
    public SubmittedThesisDTO submitThesis(CreateSubmittedThesisDTO thesisDTO) {

        //Teacher doesnt need to pass logged-in user thats for use in Student view
        //He sets the ID from the Frontend form so pass null
        User currentUser = userService.getCurrentUser();

        Thesis thesis = convertToEntity(thesisDTO, currentUser);
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


    //Change Thesis status from Approved to Defended
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
    //ENTITY -> DTO
    //
    //Entity -> Submitted Thesis DTO

    private SubmittedThesisDTO convertToSubmittedThesisDTO(Thesis thesis) {
        SubmittedThesisDTO thesisDTO = modelMapper.map(thesis, SubmittedThesisDTO.class);

        if (thesis.getStudent() != null) {
            thesisDTO.setStudentId(thesis.getStudent().getId());
            thesisDTO.setStudentNumber(thesis.getStudent().getStudentNumber());
        }

        return thesisDTO;
    }

    private UpdateSubmittedThesisDTO convertToUpdateSubmittedThesisDTO(Thesis thesis) {
        UpdateSubmittedThesisDTO thesisDTO = modelMapper.map(thesis, UpdateSubmittedThesisDTO.class);

        return thesisDTO;
    }

    private UpdateDefendedThesisDTO convertToUpdateDefendedThesisDTO(Thesis thesis) {
        UpdateDefendedThesisDTO thesisDTO = modelMapper.map(thesis, UpdateDefendedThesisDTO.class);

        return thesisDTO;
    }



    //Entity -> Approved Thesis DTO
    private ApprovedThesisDTO convertToApprovedThesisDTO(Thesis thesis) {
        ApprovedThesisDTO thesisDTO = modelMapper.map(thesis, ApprovedThesisDTO.class);

        if (thesis.getStudent() != null) {
            thesisDTO.setStudentId(thesis.getStudent().getId());
            thesisDTO.setStudentNumber(thesis.getStudent().getStudentNumber());
        }

        return thesisDTO;
    }


    //Entity -> Defended Thesis DTO
    private DefendedThesisDTO convertToDefendedThesisDTO(Thesis thesis) {
        DefendedThesisDTO thesisDTO = modelMapper.map(thesis, DefendedThesisDTO.class);

        if (thesis.getStudent() != null) {
            thesisDTO.setStudentId(thesis.getStudent().getId());
            thesisDTO.setStudentNumber(thesis.getStudent().getStudentNumber());

            thesisDTO.setGrade(thesis.getGrade());
        }

        return thesisDTO;
    }

    //Entity -> Student Thesis DTO
    private StudentThesisDTO convertToStudentThesisDTO(Thesis thesis) {
        return modelMapper.map(thesis, StudentThesisDTO.class);
    }


    //
    //
    //DTO -> ENTITY
    //
    //

    //CreateSubmittedThesisDTO -> Entity
    private Thesis convertToEntity(CreateSubmittedThesisDTO thesisDTO, User currentUser) {

        Thesis thesis = new Thesis();

        thesis.setTitle(thesisDTO.getTitle());
        thesis.setStatus(thesisDTO.getStatus());

        //Teacher View
        //Sets Student ID from Frontend Form
        if (thesisDTO.getStudentId() != null) {
            Student student = studentRepository.findById(thesisDTO.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            thesis.setStudent(student);
        } else if (currentUser.getStudent_owner() != null) {
            //Student View
            //Set Student from Authentication context - current User
            thesis.setStudent(currentUser.getStudent_owner());
        } else {
            throw new RuntimeException("Student information is missing");
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
