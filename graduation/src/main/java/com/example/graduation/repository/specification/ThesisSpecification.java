package com.example.graduation.repository.specification;

import com.example.graduation.entity.Thesis;
import com.example.graduation.entity.enums.Grade;
import com.example.graduation.entity.enums.ThesisStatus;
import org.springframework.data.jpa.domain.Specification;

public class ThesisSpecification {

    public static Specification<Thesis> titleContains(String title) {
        return (root, query, cb) ->
                title == null ? cb.conjunction() :
                        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }


    //STATUS - Submitted, Approved, Defended, Rejected
    public static Specification<Thesis> statusEquals(ThesisStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction(); // no filtering
            }
            return cb.equal(root.get("status"), status);
        };
    }



    //Thesis AUTHOR - Student Number
    public static Specification<Thesis> studentNumberEquals(String studentNumber) {
        return (root, query, cb) -> {
            if (studentNumber == null || studentNumber.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.join("student").get("studentNumber"), studentNumber);
        };
    }



    //Thesis GRADE
    public static Specification<Thesis> gradeEquals(Grade grade) {
        return (root, query, cb) -> {
            if (grade == null) {
                return cb.conjunction(); // no filtering if null
            }
            return cb.equal(root.get("grade"), grade);
        };
    }
}
