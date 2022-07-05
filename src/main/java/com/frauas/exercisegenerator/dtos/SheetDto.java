package com.frauas.exercisegenerator.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SheetDto {
    @NotNull
    private String title;

    private Boolean isPublished = false;

    private Boolean useNumericTitles = false;

    private Boolean showSolutions = false;

    @NotNull
    @NotEmpty
    private List<String> exercises;

    @NotNull
    @NotEmpty
    private List<CreateCourseDto> courses;

    @NotNull
    @NotEmpty
    private List<CreateCategoryDto> categories;
}
