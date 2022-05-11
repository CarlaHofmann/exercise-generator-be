package com.frauas.exercisegenerator.documents;

import com.frauas.exercisegenerator.enums.SolutionType;

import org.springframework.data.annotation.PersistenceConstructor;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TextSolution extends Solution {
    private String text;

    public TextSolution() {
        super(SolutionType.TEXT);
    }

    public TextSolution(String text) {
        super(SolutionType.TEXT);
        this.text = text;
    }

    @PersistenceConstructor
    public TextSolution(SolutionType type, String text) {
        super(SolutionType.TEXT);
        this.text = text;
    }
}
