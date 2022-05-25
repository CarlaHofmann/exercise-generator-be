package com.frauas.exercisegenerator.dtos;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSheetDto {
    @NotNull
    private String title;

    private LocalDateTime publishedAt;

    @NotNull
    @NotEmpty
    private List<String> exercises;

    private List<CreateCategoryDto> categories;
}
