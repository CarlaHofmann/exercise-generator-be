package com.frauas.exercisegenerator.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "exercises")
public class Exercise {

    @Id
    private String id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @DBRef
    private Author author;

    private String title;
    private String text;
    private String shortText;

    private List<String> categories;

    private List<String> images;

    private List<String> subExercises;

    @DBRef
    private List<Solution> solutions;
}
