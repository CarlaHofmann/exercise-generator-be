package com.frauas.exercisegenerator.documents;

import com.frauas.exercisegenerator.enums.SolutionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageSolution extends Solution {
    @With
    private final SolutionType type;

    private String url;

    public ImageSolution() {
        this.type = SolutionType.IMAGE;
    }

    public ImageSolution(String url) {
        this.type = SolutionType.IMAGE;
        this.url = url;
    }
}
