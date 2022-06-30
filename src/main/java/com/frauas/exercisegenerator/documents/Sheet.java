package com.frauas.exercisegenerator.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frauas.exercisegenerator.mongo.UpsertSave;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document
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

    @NotNull
    private String title;

    @DBRef
    private User author;

    @NotNull
    @NotEmpty
    @DBRef
    @UpsertSave(filters = "name")
    private List<Course> courses;

    @NotNull
    @NotEmpty
    @DBRef
    @UpsertSave(filters = "name")
    private List<Category> categories;

    @NotNull
    @NotEmpty
    @DBRef
    private List<Exercise> exercises;
}
