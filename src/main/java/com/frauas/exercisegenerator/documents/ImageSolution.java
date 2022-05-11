package com.frauas.exercisegenerator.documents;

import com.frauas.exercisegenerator.enums.SolutionType;

import org.springframework.data.annotation.PersistenceConstructor;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImageSolution extends Solution {
    private String url;

    public ImageSolution() {
        super(SolutionType.IMAGE);
    }

    public ImageSolution(String url) {
        super(SolutionType.IMAGE);
        this.url = url;
    }

    @PersistenceConstructor
    public ImageSolution(SolutionType type, String url) {
        super(SolutionType.IMAGE);
        this.url = url;
    }
}
