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
@Document(collection = "sheets")
public class Sheet {

    @Id
    private String id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @DBRef
    private Author author;

    private List<String> categories;

    @DBRef
    private List<Exercise> exercises;
}
