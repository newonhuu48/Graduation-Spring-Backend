package com.example.graduation.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Grade {
    GRADE_2_00(2.00),
    GRADE_3_00(3.00),
    GRADE_3_50(3.50),
    GRADE_4_00(4.00),
    GRADE_4_50(4.50),
    GRADE_5_00(5.00),
    GRADE_5_50(5.50),
    GRADE_6_00(6.00);

    private final double value;

    //This is in order to evade double rounding issues
    private static final double EPSILON = 0.0001;


    public static Grade fromValue(double value) {
        return Arrays.stream(values())
                .filter(g -> Math.abs(g.value - value) < EPSILON)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid grade: " + value));
    }


    @Override
    public String toString() {
        return String.format("%.2f", value);
    }
}
