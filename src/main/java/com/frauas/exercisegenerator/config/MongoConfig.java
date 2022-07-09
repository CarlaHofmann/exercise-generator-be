package com.frauas.exercisegenerator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.frauas.exercisegenerator.mongo.CascadeSaveMongoEventListener;
import com.frauas.exercisegenerator.mongo.DeleteExerciseListener;
import com.frauas.exercisegenerator.mongo.ExerciseVirtualFieldListener;
import com.frauas.exercisegenerator.mongo.SaveSheetListener;
import com.frauas.exercisegenerator.mongo.UpsertSaveMongoEventListener;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    @Bean
    ExerciseVirtualFieldListener exerciseVirtualFieldListener() {
        return new ExerciseVirtualFieldListener();
    }

    @Bean
    CascadeSaveMongoEventListener cascadeSaveMongoEventListener() {
        return new CascadeSaveMongoEventListener();
    }

    @Bean
    UpsertSaveMongoEventListener upsertSaveMongoEventListener() {
        return new UpsertSaveMongoEventListener();
    }

    @Bean
    DeleteExerciseListener deleteExerciseListener() {
        return new DeleteExerciseListener();
    }

    @Bean
    SaveSheetListener saveSheetListener() {
        return new SaveSheetListener();
    }
}
