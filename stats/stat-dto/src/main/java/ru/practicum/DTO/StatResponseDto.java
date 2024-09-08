package ru.practicum.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class StatResponseDto {
    private String app;
    private String uri;
    private Integer hits;
}

