package ru.practicum.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "likes", source = "likes")
    @Mapping(target = "dislikes", source = "dislikes")
    CommentDto toCommentDto(Comment comment);
}
