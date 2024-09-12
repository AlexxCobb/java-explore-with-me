package ru.practicum.dto.mapper;


import org.mapstruct.Mapper;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);
}
