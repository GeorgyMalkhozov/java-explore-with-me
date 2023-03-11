package ru.practicum.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.category.dao.CategoryDao;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryDao categoryDao;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
                           CategoryDao categoryCheckDao) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.categoryDao = categoryCheckDao;
    }

    public CategoryDto addCategory(NewCategoryDto dto) {
        Category category = categoryMapper.newCategoryDtoToCategory(dto);
        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email должен быть уникальными");
        }
        return categoryMapper.categoryToCategoryDto(category);
    }

    public void deleteCategory(Long id) {
        categoryDao.checkCategoryExist(id);
        categoryRepository.deleteById(id);
    }

    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        categoryDao.checkCategoryExist(id);
        Category category = categoryDao.getCategoryById(id);
        categoryMapper.updateCategoryFromDto(dto, category);
        try {
            categoryRepository.save(category);
        } catch (TransactionSystemException e) {
            throw new ValidationException("поле name не может быть пустыми");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("name должен быть уникальным");
        }
        return categoryMapper.categoryToCategoryDto(category);
    }

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from / size, size)).toList();
        return categories.stream()
                .map(categoryMapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategory(Long id) {
        categoryDao.checkCategoryExist(id);
        return categoryMapper.categoryToCategoryDto(categoryDao.getCategoryById(id));
    }
}
