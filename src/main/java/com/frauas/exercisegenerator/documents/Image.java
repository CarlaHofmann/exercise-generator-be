package com.frauas.exercisegenerator.documents;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image {
    /**
     * Absolute path to the image file on the system disk.
     */
    private String filepath;

    /**
     * Reference string which will be replaced by the filepath during LaTeX
     * generation.
     */
    private String reference;
}
