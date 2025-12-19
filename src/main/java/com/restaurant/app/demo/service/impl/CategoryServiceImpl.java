package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.entity.Category;
import com.restaurant.app.demo.repository.CategoryRepository;
import com.restaurant.app.demo.service.CategoryService;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }
}
