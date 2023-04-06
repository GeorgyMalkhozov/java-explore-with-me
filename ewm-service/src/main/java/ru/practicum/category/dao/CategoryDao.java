package ru.practicum.category.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;

@Component
public class CategoryDao {

    private final CategoryRepository categoryRepository;

    public CategoryDao(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void saveCategory(Category category) {
        try {
            categoryRepository.save(category);
        } catch (TransactionSystemException e) {
            throw new ValidationException("Название категории не может быть пустыми");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Название категории должно быть уникальным");
        }
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NoObjectsFoundException("Категория  с id = " + id + " не существует"));
    }


}
