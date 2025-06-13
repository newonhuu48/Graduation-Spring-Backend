package com.example.graduation.repository.specification;

import com.example.graduation.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {
    public static Specification<Student> firstNameContains(String firstName) {
        return (root, query, cb) ->
                firstName == null ? null :
                        cb.like(root.get("firstName"), "%" + firstName + "%");
    }

    public static Specification<Student> lastNameContains(String lastName) {
        return (root, query, cb) ->
                lastName == null ? null :
                        cb.like(root.get("firstName"), "%" + lastName + "%");
    }

    public static Specification<Student> studentNumberContains(String studentNumber) {
        return (root, query, cb) ->
                studentNumber == null ? null :
                        cb.like(root.get("firstName"), "%" + studentNumber + "%");
    }


    //ADD MORE LATER
    //


}