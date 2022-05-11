package com.frauas.exercisegenerator.dtos;

import com.frauas.exercisegenerator.documents.ImageSolution;
import com.frauas.exercisegenerator.documents.Solution;
import com.frauas.exercisegenerator.documents.TextSolution;
import com.frauas.exercisegenerator.enums.SolutionType;

import org.modelmapper.AbstractConverter;

import lombok.Data;

@Data
public class CreateSolutionDto {

    private SolutionType type;

    private String content;

    public static class SolutionConverter extends AbstractConverter<CreateSolutionDto, Solution> {

        @Override
        protected Solution convert(CreateSolutionDto source) {
            if (source == null) {
                return null;
            }

            Solution destination = null;

            switch (source.getType()) {
                case IMAGE:
                    destination = new ImageSolution(source.getContent());
                    break;

                case TEXT:
                default:
                    destination = new TextSolution(source.getContent());
                    break;
            }

            return destination;
        }

    }
}
