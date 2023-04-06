package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

 public interface UserService {
    
     UserDto addUser(NewUserRequest dto);

     void deleteUser(Long id);

     List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
