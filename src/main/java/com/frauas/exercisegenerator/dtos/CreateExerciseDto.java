package com.frauas.exercisegenerator.dtos;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateExerciseDto {
    @NotNull
    private String title;

    @NotNull
    private String text;

    private String shortText;

    @NotNull
    private List<String> categories;

    private List<String> images;

    private List<CreateExerciseDto> subExercises;

    private List<CreateSolutionDto> solutions;
}
