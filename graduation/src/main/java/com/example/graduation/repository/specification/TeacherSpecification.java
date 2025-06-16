package com.example.graduation.repository.specification;

import com.example.graduation.entity.Teacher;
import org.springframework.data.jpa.domain.Specification;

public class TeacherSpecification {
    public static Specification<Teacher> firstNameContains(String firstName) {
        return (root, query, cb) ->
                firstName == null ? null :
                        cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Teacher> lastNameContains(String lastName) {
        return (root, query, cb) ->
                lastName == null ? null :
                        cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Teacher> teacherNumberContains(String teacherNumber) {
        return (root, query, cb) ->
                teacherNumber == null ? null :
                        cb.like(root.get("teacherNumber"), "%" + teacherNumber + "%");
    }


    //ADD MORE LATER
    //


}