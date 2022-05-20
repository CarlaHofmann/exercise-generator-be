package com.frauas.exercisegenerator.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateExerciseDto {

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
    private List<CreateCategoryDto> categories;

    private List<CreateCategoryDto> hiddenCategories;
}
