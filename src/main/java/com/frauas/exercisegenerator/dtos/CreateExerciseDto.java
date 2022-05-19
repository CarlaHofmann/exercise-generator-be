package com.frauas.exercisegenerator.dtos;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateExerciseDto extends AbstractCreateExerciseDto {

    @NotNull
    @NotEmpty
    private List<CreateCategoryDto> categories;

    private List<CreateSubExerciseDto> subExercises;
}
