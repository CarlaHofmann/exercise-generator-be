package com.frauas.exercisegenerator.documents;

import com.frauas.exercisegenerator.enums.SolutionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TextSolution extends Solution {
    @With
    private final SolutionType type;

    private String text;

    public TextSolution() {
        this.type = SolutionType.TEXT;
    }

    public TextSolution(String text) {
        this.type = SolutionType.IMAGE;
        this.text = text;
    }
}
