package moviemanager.service;

// prefer DTO here
import moviemanager.entity.Movie;
import moviemanager.entity.Person;

import java.util.List;

public interface IMovieService {
    List<Movie> getAll();
    boolean addMovie(Movie movie, Person director);
}
