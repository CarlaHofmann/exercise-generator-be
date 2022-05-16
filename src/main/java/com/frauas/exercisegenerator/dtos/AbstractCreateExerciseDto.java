package com.frauas.exercisegenerator.dtos;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public abstract class AbstractCreateExerciseDto {
    @NotNull
    private String title;

    @NotNull
    private String text;

    private String shortText;

    private ArrayList<String> images;

    @NotNull
    @NotEmpty
    private ArrayList<CreateSolutionDto> solutions;
}
