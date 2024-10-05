package ru.practicum.dto.user;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserParam {
    private List<Long> userIds;

    @Min(0)
    private Integer from = 0;
    @Min(1)
    private Integer size = 10;
}
