package com.frauas.exercisegenerator.dtos;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateCourseDto {

    @NotNull
    private String name;
}
