package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Like;

@Transactional(readOnly = true)
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
}