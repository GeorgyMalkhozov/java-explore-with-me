package ru.practicum.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.user.dao.UserDao;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserDao userDao) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userDao = userDao;
    }

    @Transactional
    public UserDto addUser(NewUserRequest dto) {
        User user = userMapper.newUserRequestToUser(dto);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email должен быть уникальными");
        }
        return userMapper.userToUserDto(user);
    }

    public void deleteUser(Long id) {
        userDao.getUserById(id);
        userRepository.deleteById(id);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids == null) {
            users = userRepository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            users = userRepository.findAllByIdIsIn(ids, PageRequest.of(from / size, size));
        }
        return users.stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }
}
