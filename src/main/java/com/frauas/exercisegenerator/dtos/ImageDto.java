package com.frauas.exercisegenerator.dtos;

import lombok.Data;

@Data
public class ImageDto {
    /**
     * Reference string which will be replaced by the filepath during LaTeX
     * generation.
     */
    private String reference;

    /**
     * Base64 encoded image content.
     * <p>
     * Needs to include filetype information (allowed types: .jpg, .jpeg, .png).
     */
    private String content;
}
