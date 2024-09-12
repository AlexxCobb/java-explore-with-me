package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.repository.StatisticRepository;
import ru.practicum.dto.StatRequestDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.constants.Constants;
import ru.practicum.exception.BadRequestException;
import ru.practicum.model.StatShort;
import ru.practicum.model.StatShortMapper;
import ru.practicum.model.StatisticMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository repository;

    public void createHit(StatRequestDto statRequestDto) {
        var statHit = StatisticMapper.toStatistic(statRequestDto);
        repository.save(statHit);
    }

    public List<StatResponseDto> getStatHits(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDateTime = LocalDateTime.parse(start, Constants.DATE_PATTERN);
        LocalDateTime endDateTime = LocalDateTime.parse(end, Constants.DATE_PATTERN);
        if (startDateTime.isAfter(endDateTime)) {
            throw new BadRequestException("Неверно указаны даты начала и конца для выгрузки статистики, start= " + start + " end= " + end);
        }

        List<StatShort> stats;

        if (uris == null) {
            if (unique) {
                stats = repository.findAllUrisWithUniqueIp(startDateTime, endDateTime);
            } else {
                stats = repository.findAllUris(startDateTime, endDateTime);
            }
        } else {
            if (unique) {
                stats = repository.findUrisWithUniqueIp(uris, startDateTime, endDateTime);
            } else {
                stats = repository.findUris(uris, startDateTime, endDateTime);
            }
        }
        return !stats.isEmpty() ? stats.stream().map(StatShortMapper::toStatResponseDto).collect(Collectors.toList()) : Collections.emptyList();
    }
}