package com.frauas.exercisegenerator.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateCourseDto {

    @NotNull
    private String name;
}
