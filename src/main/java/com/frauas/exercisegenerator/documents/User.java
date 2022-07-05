package com.frauas.exercisegenerator.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private boolean admin;
}
