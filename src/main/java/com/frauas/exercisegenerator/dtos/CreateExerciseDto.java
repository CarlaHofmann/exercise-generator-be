package com.frauas.exercisegenerator.dtos;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.frauas.exercisegenerator.documents.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateExerciseDto extends AbstractCreateExerciseDto {

    @NotNull
    @NotEmpty
    private ArrayList<CreateCategoryDto> categories;

    private ArrayList<CreateSubExerciseDto> subExercises;
}
