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
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document
public class Exercise {

    @Id
    private String id;

    @JsonIgnore
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonIgnore
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    private Boolean isPublished = false;

    private Boolean isUsed = false;

    @DBRef
    private User author;

    @NotNull
    private String title;

    private String shortDescription = "";
    private String note = "";

    @NotNull
    @NotEmpty
    private List<String> texts;

    @NotNull
    @NotEmpty
    private List<String> solutions;

    private List<Image> images = new ArrayList<>();

    @DBRef
    @UpsertSave(filters = "name")
    private List<Course> courses;

    @DBRef
    @UpsertSave(filters = "name")
    private List<Category> categories;
}
