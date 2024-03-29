package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    User newUserRequestToUser(NewUserRequest newUserRequest);
}
