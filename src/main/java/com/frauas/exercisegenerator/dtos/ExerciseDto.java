package com.frauas.exercisegenerator.dtos;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ExerciseDto {
    @NotNull
    private String title;

    private String note = "";

    private String shortDescription = "";

    private Boolean isPublished = false;

    private Boolean isUsed = false;

    private List<ImageDto> images = new ArrayList<>();

    @NotNull
    @NotEmpty
    private List<String> texts;

    @NotNull
    @NotEmpty
    private List<String> solutions;

    @NotNull
    @NotEmpty
    private List<CreateCourseDto> courses;

    @NotNull
    @NotEmpty
    private List<CreateCategoryDto> categories;
}
