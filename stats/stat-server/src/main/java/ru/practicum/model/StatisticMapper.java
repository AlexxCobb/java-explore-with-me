package ru.practicum.model;

import org.springframework.stereotype.Component;
import ru.practicum.DTO.StatRequestDto;
import ru.practicum.constants.Constants;

import java.time.LocalDateTime;

@Component
public class StatisticMapper {

    public static Statistic toStatistic(StatRequestDto statRequestDto) {
        var dateTimeString = statRequestDto.getTimestamp();
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, Constants.DATE_PATTERN);
        return new Statistic(null, statRequestDto.getApp(), statRequestDto.getUri(), statRequestDto.getIp(), dateTime);
    }
}
