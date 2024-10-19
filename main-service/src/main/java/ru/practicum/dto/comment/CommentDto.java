package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.constants.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
    private LocalDateTime created;
    private String text;
    private Long eventId;
    private Long authorId;
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer likes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer dislikes;
}
