package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Category;

@Transactional(readOnly = true)
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsById(Integer id);
}