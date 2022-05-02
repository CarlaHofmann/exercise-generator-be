package com.frauas.exercisegenerator.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "solutions")
public class Solution {

    @Id
    private String id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> texts;
    private List<String> images;
}
