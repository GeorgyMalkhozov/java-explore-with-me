package ru.practicum.category.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;

@Component
public class CategoryDao {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryDao(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
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

    public void checkCategoryHasNoEventsBeforeDelete(Long categoryId) {
        if (!eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException("Нельзя удалить категорию к которой привязаны события");
        }
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
}
