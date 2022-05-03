package com.frauas.exercisegenerator.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CreateSolutionDto {
    private List<String> texts;
    private List<String> images;
}
