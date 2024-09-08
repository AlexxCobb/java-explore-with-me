package ru.practicum.model;

import org.springframework.stereotype.Component;
import ru.practicum.DTO.StatResponseDto;

@Component
public class StatShortMapper {

    public static StatResponseDto toStatResponseDto(StatShort statShort) {
        return new StatResponseDto(statShort.getApp(), statShort.getUri(), statShort.getHits());
    }
}
