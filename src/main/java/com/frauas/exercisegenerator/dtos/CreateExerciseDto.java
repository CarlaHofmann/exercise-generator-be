package com.frauas.exercisegenerator.dtos;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateExerciseDto extends CreateSubExerciseDto {

    @NotNull
    @NotEmpty
    private ArrayList<String> categories;

    private ArrayList<CreateSubExerciseDto> subExercises;
}
