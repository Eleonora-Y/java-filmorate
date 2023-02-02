package ru.yandex.practicum.filmorate.utilit;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
public class WarnAndThrowException {
    public static void logWarnAndThrowException(String message) {
        log.warn(message);
        throw new ValidationException(message);
    }
}
