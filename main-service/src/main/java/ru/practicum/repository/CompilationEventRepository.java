package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.CompilationEvent;

import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    List<CompilationEvent> findAllByCompilation_Id(Long compId);

    void deleteAllByCompilation_Id(Long compId);

    List<CompilationEvent> findByCompilation_IdIn(Collection<Long> ids);
}