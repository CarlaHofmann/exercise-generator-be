package com.frauas.exercisegenerator.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.repositories.SheetRepository;

public class ExerciseVirtualFieldListener extends AbstractMongoEventListener<Exercise> {
    @Autowired
    SheetRepository sheetRepository;

    @Override
    public void onAfterConvert(AfterConvertEvent<Exercise> event) {
        super.onAfterConvert(event);

        Exercise exercise = event.getSource();
        List<Sheet> sheets = sheetRepository.findByExercisesContaining(exercise.getId());

        exercise.setIsUsed(!sheets.isEmpty());
    }
}
