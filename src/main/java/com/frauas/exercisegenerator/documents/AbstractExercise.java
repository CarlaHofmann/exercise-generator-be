package com.frauas.exercisegenerator.documents;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class AbstractExercise {
    private String title;
    private String text;
    private String shortText;

    private List<String> images;

    @Schema(oneOf = { TextSolution.class, ImageSolution.class }, discriminatorProperty = "type")
    private List<Solution> solutions;

}
