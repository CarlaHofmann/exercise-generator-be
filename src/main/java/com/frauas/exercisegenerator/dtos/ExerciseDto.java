package com.frauas.exercisegenerator.dtos;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ExerciseDto {

    @NotNull
    private String title;

    private String note;

    private String shortDescription;

    @NotNull
    @NotEmpty
    private List<String> texts;

    @NotNull
    @NotEmpty
    private List<String> solutions;

    private List<String> images;

    @NotNull
    @NotEmpty
    private List<CreateCourseDto> courses;

    @NotNull
    @NotEmpty
    private List<CreateCategoryDto> categories;
}
