package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.mapper.CategoryMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper categoryMapper;

    public CategoryDto createCategory(CategoryDto categoryDto) {
        var category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(repository.save(category));
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        if (repository.existsById(id)) {
            var cat = categoryMapper.toCategory(categoryDto);
            return categoryMapper.toCategoryDto(repository.save(cat));
        } else {
            throw new NotFoundException("Категория с id " + id + " не найдена.");
        }
    }

    public void deleteCategory(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException("Категория с id " + id + " не найдена.");
        }
    }
}
