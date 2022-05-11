package com.frauas.exercisegenerator.services;

import java.util.List;
import java.util.Optional;

import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.dtos.CreateCategoryDto;
import com.frauas.exercisegenerator.repositories.CategoryRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(CreateCategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);

        return categoryRepository.save(category);
    }
}
