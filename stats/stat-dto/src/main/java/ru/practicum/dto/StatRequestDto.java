package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.dto.validator.ValidDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class StatRequestDto {

    @NotBlank
    private String app;

    @NotNull
    private String uri;

    @NotBlank
    private String ip;

    @NotBlank
    @ValidDate
    private String timestamp;
}