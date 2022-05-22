package com.frauas.exercisegenerator.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateCategoryDto {

    @NotNull
    private String name;

    @NotNull
    private Boolean isHidden;
}
