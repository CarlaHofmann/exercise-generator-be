package com.frauas.exercisegenerator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class SheetDto {

    private Boolean isPublished;

    private Boolean useNumericTitles;

    private Boolean showSolution;

    @NotNull
    private String title;

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
