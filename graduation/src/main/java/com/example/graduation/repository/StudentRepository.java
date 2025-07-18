package com.example.graduation.repository;

import com.example.graduation.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository
        extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {


}