package com.frauas.exercisegenerator.helpers;

import java.util.ArrayList;
import java.util.List;

import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.dtos.CreateCategoryDto;
import com.frauas.exercisegenerator.repositories.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryUpsertHelper {
    @Autowired
    CategoryRepository categoryRepository;

    public ArrayList<Category> upsertCategoriesFromDto(List<CreateCategoryDto> dtoCategories) {
        return this.upsertCategoriesFromDto(dtoCategories, false);
    }

    public ArrayList<Category> upsertCategoriesFromDto(List<CreateCategoryDto> dtoCategories, boolean isHidden) {
        ArrayList<Category> categories = new ArrayList<>();

        dtoCategories.forEach(categoryDto -> {
            Category category = this.categoryRepository.findByNameAndIsHidden(categoryDto.getName(), isHidden);

            if (category == null) {
                category = Category.builder()
                        .name(categoryDto.getName())
                        .isHidden(isHidden)
                        .build();
                category = this.categoryRepository.save(category);
            }

            categories.add(category);
        });

        return categories;
    }
}
