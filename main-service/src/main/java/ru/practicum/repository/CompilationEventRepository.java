package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Compilation;
import ru.practicum.model.CompilationEvent;

import java.util.Collection;
import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    List<CompilationEvent> findAllByCompilation_Id (Long compId);

    void deleteAllByCompilation_Id(Long compId);

    void deleteByCompilation(Compilation compilation);

    List<CompilationEvent> findByCompilation_IdIn(Collection<Long> ids);
}
