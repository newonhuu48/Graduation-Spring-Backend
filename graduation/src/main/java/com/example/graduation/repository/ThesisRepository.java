package com.example.graduation.repository;

import com.example.graduation.entity.Thesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ThesisRepository
        extends JpaRepository<Thesis, Long>,
        JpaSpecificationExecutor<Thesis> {


}
