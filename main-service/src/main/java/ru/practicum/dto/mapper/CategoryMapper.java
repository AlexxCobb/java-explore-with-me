package ru.practicum.dto.mapper;

import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);
}
