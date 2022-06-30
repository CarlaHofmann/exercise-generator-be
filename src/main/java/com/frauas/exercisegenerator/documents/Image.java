package com.frauas.exercisegenerator.documents;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    /**
     * Absolute path to the image file on the system disk.
     * Property will not be serialized in JSON responses.
     */
    @JsonIgnore
    private String filepath;

    /**
     * Reference string which will be replaced by the filepath during LaTeX
     * generation.
     */
    private String reference;

    /**
     * Image content as base64 encoded string.
     * Property will not be persisted to the database.
     */
    @Transient
    private String content;
}
