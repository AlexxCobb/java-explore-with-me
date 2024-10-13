package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.User;

import java.util.List;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsById(Long id);

    List<User> findByIdIn(List<Long> ids, Pageable pageable);
}