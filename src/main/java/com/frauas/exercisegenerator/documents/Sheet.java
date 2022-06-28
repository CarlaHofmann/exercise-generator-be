package com.frauas.exercisegenerator.documents;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frauas.exercisegenerator.mongo.UpsertSave;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document()
public class Sheet {

    @Id
    private String id;

    @JsonIgnore
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    private Boolean isPublished = false;

    private Boolean useNumericTitles = false;

    private Boolean showSolutions = false;

    private String title;

    @DBRef
    private Author author;

    @DBRef
    @UpsertSave(filters = "name")
    private List<Course> courses;

    @DBRef
    @UpsertSave(filters = "name")
    private List<Category> categories;

    @DBRef(lazy = true)
    private List<Exercise> exercises;
}
