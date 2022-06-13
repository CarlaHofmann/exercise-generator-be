package com.frauas.exercisegenerator.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document()
public class User {

    @Id
    private String id;

    private String userName;
    private String password;
}
