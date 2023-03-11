package ru.practicum.category.dao;

import org.springframework.stereotype.Component;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exceptions.NoObjectsFoundException;

@Component
public class CategoryDao {

    private final CategoryRepository categoryRepository;

    public CategoryDao(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void checkCategoryExist(Long categoryId) {
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new NoObjectsFoundException("Категория с id = " + categoryId + " не существует");
        }
    }

    public Category getCategoryById(Long id) {
        checkCategoryExist(id);
        return categoryRepository.getById(id);
    }
}
