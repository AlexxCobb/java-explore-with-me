package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank
    @Email
    @Size(min = 6,max = 254)
    private String email;

    @NotBlank
    @Size(min = 2,max = 250)
    private String name;
}
//email*	string
//maxLength: 254
//minLength: 6
//example: ivan.petrov@practicummail.ru
//
//Почтовый адрес
//name*	string
//maxLength: 250
//minLength: 2
//example: Иван Петров
//
//Имя