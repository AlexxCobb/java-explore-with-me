package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserMapper;
import ru.practicum.dto.UserParam;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repository.UserRepository;
import ru.practicum.utils.PaginationServiceClass;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        var user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    public List<UserDto> getUsers(UserParam userParam) {
        Pageable page = PaginationServiceClass.pagination(userParam.getFrom(), userParam.getSize());
        var users = userRepository.findByIdIn(userParam.getUserIds(), page);
        return !users.isEmpty() ? users : Collections.emptyList();
    }

}
