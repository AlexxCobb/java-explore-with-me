package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.StatShort;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    @Query("select s.app as app, s.uri as uri, count(distinct s.ip) as hits from Statistic s " +
            "where s.createdDate between :start and :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatShort> findAllUrisWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select s.app as app, s.uri as uri, count(s.ip) as hits from Statistic s " +
            "where s.createdDate between :start and :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatShort> findAllUris(LocalDateTime start, LocalDateTime end);

    @Query("select s.app as app, s.uri as uri, count(distinct s.ip) as hits from Statistic s " +
            "where s.uri in :uris and s.createdDate between :start and :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatShort> findUrisWithUniqueIp(@Param("uris") List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select s.app as app, s.uri as uri, count(s.ip) as hits from Statistic s " +
            "where s.uri in :uris and s.createdDate between :start and :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatShort> findUris(@Param("uris") List<String> uris, LocalDateTime start, LocalDateTime end);
}
