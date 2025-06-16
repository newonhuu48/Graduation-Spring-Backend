package com.example.graduation.exception;

public class ThesisNotFoundException extends RuntimeException {
    public ThesisNotFoundException(Long id) {
        super("Thesis with ID " + id + " not found.");
    }
}
