package com.example.graduation.repository.specification;

import com.example.graduation.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {
    public static Specification<Student> firstNameContains(String firstName) {
        return (root, query, cb) ->
                firstName == null ? null :
                        cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Student> lastNameContains(String lastName) {
        return (root, query, cb) ->
                lastName == null ? null :
                        cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Student> studentNumberContains(String studentNumber) {
        return (root, query, cb) ->
                studentNumber == null ? null :
                        cb.equal(root.get("studentNumber"), studentNumber);
    }


    //ADD MORE LATER
    //


}