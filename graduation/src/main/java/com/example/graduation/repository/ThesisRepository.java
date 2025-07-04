package com.example.graduation.repository;

import com.example.graduation.entity.Thesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThesisRepository
        extends JpaRepository<Thesis, Long>,
        JpaSpecificationExecutor<Thesis> {


    //To load Thesis on Student View if they have one
    Optional<Thesis> findByStudentId(Long studentId);
}
