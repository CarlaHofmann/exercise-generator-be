package com.frauas.exercisegenerator.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document()
public class Exercise {
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @DBRef
    private Author author;

    private String title;
    private String shortDescription;
    private String note;

    private List<String> texts;

    private List<String> solutions;

    private List<String> images;

    @DBRef
    private List<Course> courses;

    @DBRef
    private List<Category> categories;
}
