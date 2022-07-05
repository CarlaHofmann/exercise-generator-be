package com.frauas.exercisegenerator.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;

public class SaveSheetListener extends AbstractMongoEventListener<Sheet> {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Override
    public void onAfterSave(AfterSaveEvent<Sheet> event) {
        super.onAfterSave(event);

        Sheet sheet = event.getSource();

        if (sheet.getIsPublished()) {
            List<Exercise> exercises = sheet.getExercises();

            for (Exercise exercise : exercises) {
                if (!exercise.getIsPublished()) {
                    exercise.setIsPublished(true);
                    exercise.setPublishedAt(sheet.getPublishedAt());
                }

                exerciseRepository.save(exercise);
            }
        }
    }
}
