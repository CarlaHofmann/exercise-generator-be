package com.frauas.exercisegenerator.documents;

import com.frauas.exercisegenerator.enums.SolutionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TextSolution extends Solution {
    private SolutionType type = SolutionType.TEXT;

    private String text;
}
