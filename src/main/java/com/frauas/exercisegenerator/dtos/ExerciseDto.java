package com.frauas.exercisegenerator.dtos;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ExerciseDto {

    private Boolean isPublished;
    private Boolean isUsed;

    @NotNull
    private String title;

    private String note;

    private String shortDescription;

    private List<ImageDto> images;

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
