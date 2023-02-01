package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void createNewFilm() {
        Film newFilm1 = new Film(0,
                "Film1",
                "Description Film1",
                LocalDate.of(1980, Month.JANUARY, 17),
                140);
        filmController.create(newFilm1);
        assertFalse(filmController.getFilms().isEmpty());
    }

    @Test
    void createNewFilmWithoutNameIsException() {
        Film newFilm1 = new Film(0,
                "",
                "Description Film1",
                LocalDate.of(1980, Month.JANUARY, 17),
                (140));
        assertThrows(ValidationException.class, () -> filmController.create(newFilm1));
    }

    @Test
    void createNewFilmWithDescriptionLength200() {
        Film newFilm1 = new Film(0,
                "Film1",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                LocalDate.of(1980, Month.JANUARY, 17),
                140);
        filmController.create(newFilm1);
        assertFalse(filmController.getFilms().isEmpty());
    }


    @Test
    void createNewFilmWithDescriptionLength201IsException() {
        Film newFilm1 = new Film(0,
                "Film1",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB",
                LocalDate.of(1980, Month.JANUARY, 17),
                140);
        assertThrows(ValidationException.class, () -> filmController.create(newFilm1));
    }

    @Test
    void createNewFilmWithDescriptionLength0IsException() {
        Film newFilm1 = new Film(0,
                "Film1",
                "",
                LocalDate.of(1980, Month.JANUARY, 17),
                140);
        assertThrows(ValidationException.class, () -> filmController.create(newFilm1));
    }

    @Test
    void createNewFilmWithOldReleaseDateIsException() {
        Film newFilm1 = new Film(0,
                "Film1",
                "Description Film1",
                LocalDate.of(1895, Month.DECEMBER, 27),
                140);
        assertThrows(ValidationException.class, () -> filmController.create(newFilm1));
    }

    @Test
    void createNewFilmWithReleaseDate_1895_12_28() {
        Film newFilm1 = new Film(0,
                "Film1",
                "Description Film1",
                LocalDate.of(1895, Month.DECEMBER, 28),
                140);
        filmController.create(newFilm1);
        assertFalse(filmController.getFilms().isEmpty());
    }

    @Test
    void createNewFilmWithNegativeDurationIsException() {
        Film newFilm1 = new Film(0,
                "Film1",
                "Description Film1",
                LocalDate.of(1980, Month.JANUARY, 17),
                -190);
        assertThrows(ValidationException.class, () -> filmController.create(newFilm1));
    }

    @Test
    void update() {
        Film newFilm1 = new Film(0,
                "Film1",
                "Description Film1",
                LocalDate.of(1980, Month.JANUARY, 17),
                140);
        Film newFilm2 = new Film(1,
                "Film1Update",
                "Description Film1",
                LocalDate.of(1980, Month.JANUARY, 17),
                140);
        filmController.create(newFilm1);
        filmController.update(newFilm2);
        assertEquals("Film1Update", filmController.getFilms().get(1).getName());
    }

    @Test
    void findAll() {
        Film newFilm1 = new Film(0,
                "Film1",
                "Description Film1",
                LocalDate.of(1980, Month.JANUARY, 17),
                140);
        filmController.create(newFilm1);
        assertEquals(1, filmController.getFilms().size());
    }
}