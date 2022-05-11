package com.frauas.exercisegenerator.documents;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubExercise {
    private String title;
    private String text;
    private String shortText;

    private List<String> images;

    private List<Solution> solutions;

}
