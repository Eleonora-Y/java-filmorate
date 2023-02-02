package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.utilit.WarnAndThrowException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.id.UserId;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
@Data
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserId userId = new UserId();

    private static final LocalDate loc = LocalDate.now();

    public void validate(User user) {

        if (user.getEmail().isEmpty() ||
                user.getEmail() == null ||
                !user.getEmail().contains("@")) {
            WarnAndThrowException.logWarnAndThrowException("Адрес эл. почты пустой или не хватает символа @");
        }

        if (user.getLogin().isBlank()) {
            WarnAndThrowException.logWarnAndThrowException("Логин пустой или содержит пробелы");
        }

        if (user.getBirthday().isAfter(loc)) {
            WarnAndThrowException.logWarnAndThrowException("Дата рождения указана неверно");
        }

    }

    @PostMapping()
    public User create(@RequestBody User user) {

        if (users.containsKey(user.getId())) {
            WarnAndThrowException.logWarnAndThrowException("Пользователь уже существует");
        }
        validate(user);
        user.setId(userId.getUserId());

        if (user.getName() == null || user.getName().isEmpty()) {
            String a = user.getLogin();
            user.setName(a);
            users.put(user.getId(), user);
            log.info("Добавлен : {}", user);
        }

        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }


    @PutMapping()
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            WarnAndThrowException.logWarnAndThrowException("Пользователь не существует");
        }


        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        validate(user);
        log.info("Добавлен пользователь: {}", user);
        users.put(user.getId(), user);

        return user;
    }

    @GetMapping()
    public List<User> getAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }


}

