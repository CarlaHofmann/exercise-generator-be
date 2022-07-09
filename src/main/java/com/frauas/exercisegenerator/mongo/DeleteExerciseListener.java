package com.frauas.exercisegenerator.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Image;
import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.SheetRepository;
import com.frauas.exercisegenerator.services.ImageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteExerciseListener extends AbstractMongoEventListener<Exercise> {
    @Autowired
    SheetRepository sheetRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    ImageService imageService;

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

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Exercise> event) {
        super.onBeforeDelete(event);

        String exerciseId = event.getSource()
                .getObjectId("_id")
                .toString();

        Optional<Exercise> exercise = exerciseRepository.findById(exerciseId);

        if (exercise.isPresent()) {
            List<Image> images = exercise.get().getImages();

            try {
                // Cleanup exercise images before delete
                images.forEach(image -> imageService.deleteImageFile(image));
            } catch (Exception e) {
                log.warn("Error during image deletion of exercise with id'" + exerciseId + "'", e);
            }
        }
    }
}
