package com.frauas.exercisegenerator.enums;

public enum SolutionType {
    IMAGE("image"),
    TEXT("text");

    public final String type;

    SolutionType(String type) {
        this.type = type;
    }
}
