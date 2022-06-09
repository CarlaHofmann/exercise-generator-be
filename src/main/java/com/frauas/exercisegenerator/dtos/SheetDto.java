package com.frauas.exercisegenerator.dtos;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SheetDto {
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
