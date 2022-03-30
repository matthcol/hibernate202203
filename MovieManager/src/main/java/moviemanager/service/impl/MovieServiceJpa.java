package moviemanager.service.impl;

import moviemanager.entity.Movie;
import moviemanager.entity.Person;
import moviemanager.service.IMovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@Transactional
public class MovieServiceJpa implements IMovieService {

    @Override
    public List<Movie> getAll() {
        return List.of();
    }

    @Transactional
    @Override
    public boolean addMovie(Movie movie, Person director) {
        return false;
    }
}
