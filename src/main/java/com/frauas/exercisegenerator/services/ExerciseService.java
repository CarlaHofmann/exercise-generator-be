package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.User;
import com.frauas.exercisegenerator.dtos.CreateExerciseDto;
import com.frauas.exercisegenerator.repositories.CategoryRepository;
import com.frauas.exercisegenerator.repositories.ExerciseRepository;
import com.frauas.exercisegenerator.repositories.UserRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ExerciseService {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    ModelMapper modelMapper;

    public ExerciseService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        CategoryIdConverter categoryIdConverter = new CategoryIdConverter();

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
        Optional<User> authorOpt = this.userRepository.findByUsername("default");
        User author;

        if (authorOpt.isPresent() == false) {
            author = User.builder().username("default").build();
            author = this.userRepository.save(author);
        }
        else {
            author = authorOpt.get();
        }


        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<Category> hiddenCategories = new ArrayList<>();
        Stream.concat(exerciseDto.getCategories().stream(), exerciseDto.getHiddenCategories().stream())
                .forEach(categoryDto -> {
                    Category category = this.categoryRepository.findByNameAndIsHidden(categoryDto.getName(), categoryDto.getIsHidden());

                    if (category == null) {
                        category = Category.builder().name(categoryDto.getName()).isHidden(categoryDto.getIsHidden()).build();
                        category = this.categoryRepository.save(category);
                    }

                    if(category.getIsHidden()){
                        hiddenCategories.add(category);
                    }else{
                        categories.add(category);
                    }
                });


        Exercise exercise = this.modelMapper.map(exerciseDto, Exercise.class);

        exercise.setAuthor(author);
        exercise.setCategories(categories);
        exercise.setHiddenCategories(hiddenCategories);

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
