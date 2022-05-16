package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.Author;
import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.dtos.CreateSolutionDto.SolutionConverter;
import com.frauas.exercisegenerator.repositories.AuthorRepository;
import com.frauas.exercisegenerator.repositories.CategoryRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    CategoryRepository categoryRepository;

    ModelMapper modelMapper;

    public ExerciseService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        SolutionConverter solutionConverter = new SolutionConverter();
        CategoryIdConverter categoryIdConverter = new CategoryIdConverter();

        this.modelMapper.addConverter(solutionConverter);
        this.modelMapper.addConverter(categoryIdConverter);
    }

    public List<Exercise> getAllExercises() {
        return this.exerciseRepository.findAll();
    }

    public Optional<Exercise> getExerciseById(String id) {
        return this.exerciseRepository.findById(id);
    }

    public Exercise createExerciseFromDto(CreateExerciseDto exerciseDto) {
        // TODO: Use actual author resolution via login credentials
        Author author = this.authorRepository.findByName("default");

        if (author == null) {
            author = Author.builder().name("default").build();
            author = this.authorRepository.save(author);
        }


        ArrayList<Category> categories = new ArrayList<>();
        exerciseDto.getCategories().forEach(categoryDto -> {
            Category category = this.categoryRepository.findByName(categoryDto.getName());

            if(category == null){
                category = Category.builder().name(categoryDto.getName()).build();
                category = this.categoryRepository.save(category);
            }

            categories.add(category);
        });


        Exercise exercise = this.modelMapper.map(exerciseDto, Exercise.class);

        exercise.setAuthor(author);
        exercise.setCategories(categories);

        return this.exerciseRepository.save(exercise);
    }

    public class CategoryIdConverter extends AbstractConverter<String, Category> {
        @Override
        protected Category convert(String source) {
            if (source != null) {
                Optional<Category> optionalCategory = categoryRepository.findById(source);

                if (optionalCategory.isPresent())
                    return optionalCategory.get();
            }

            return null;
        }

    }
}
