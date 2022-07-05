package com.frauas.exercisegenerator.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.repositories.SheetRepository;

public class DeleteExerciseListener extends AbstractMongoEventListener<Exercise> {
    @Autowired
    SheetRepository sheetRepository;

    @Override
    public void onAfterDelete(AfterDeleteEvent<Exercise> event) {
        super.onAfterDelete(event);

        String exerciseId = event.getSource()
                .getObjectId("_id")
                .toString();

        List<Sheet> sheets = sheetRepository.findByExercisesContaining(exerciseId);

        for (Sheet sheet : sheets) {
            List<Exercise> exercises = sheet.getExercises();

            exercises.removeIf((exercise) -> exercise.getId() == exerciseId);

            // Empty sheets will be unpublished
            if (exercises.isEmpty()) {
                sheet.setIsPublished(false);
                sheet.setPublishedAt(null);
            }

            sheetRepository.save(sheet);
        }
    }
}
