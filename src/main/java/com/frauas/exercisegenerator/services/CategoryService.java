package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.Category;
import com.frauas.exercisegenerator.dtos.CreateCategoryDto;
import com.frauas.exercisegenerator.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND));
    }

    public Category createCategory(CreateCategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);

        return categoryRepository.save(category);
    }

    public void deleteCategoryById(String id) {
        categoryRepository.deleteById(id);
    }
}
