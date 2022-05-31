package com.frauas.exercisegenerator.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {

    @Id
    private String id;

    //@Indexed(unique = true)
    private String username;

    private String password;
}
