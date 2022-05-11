package com.frauas.exercisegenerator.documents;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubExercise {
    private String title;
    private String text;
    private String shortText;

    private ArrayList<String> images;

    private ArrayList<Solution> solutions;

}
