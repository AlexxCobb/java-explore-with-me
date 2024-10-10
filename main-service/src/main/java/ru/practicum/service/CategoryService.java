package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.mapper.CategoryMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.utils.PaginationServiceClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        var category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(repository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        var catDto = findCategoryById(id);
        if (!catDto.getName().equals(categoryDto.getName())) {
            var cat = categoryMapper.toCategory(categoryDto);
            return categoryMapper.toCategoryDto(repository.save(cat));
        } else {
            return catDto;
        }
    }

    public void deleteCategory(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException("Категория с id " + id + " не найдена.");
        }
    }

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        var page = PaginationServiceClass.pagination(from, size);
        var categories = repository.findAll(page);
        return !categories.isEmpty() ? categories.stream().map(categoryMapper::toCategoryDto).collect(Collectors.toList()) : Collections.emptyList();
    }

    public CategoryDto findCategoryById(Integer id) {
        var cat = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Категория с id " + id + " не найдена."));
        return categoryMapper.toCategoryDto(cat);
    }
}