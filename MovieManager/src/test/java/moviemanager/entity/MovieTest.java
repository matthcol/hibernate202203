package moviemanager.entity;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieTest {

    @Autowired
    TestEntityManager entityManager;
    // EntityManager entityManager;

    @Test
    void testSave(){
        Movie movie = new Movie("The Power of the Dog", 2021);
        entityManager.persist(movie);
        assertAll(
                ()->assertNotNull(movie.getId()),
                ()->assertEquals("The Power of the Dog", movie.getTitle()),
                ()->assertEquals(2021, movie.getYear()),
                ()->assertNull(movie.getDuration())
        );
        System.out.println(movie);
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(Color.class)
    void testSaveFull(Color color){
        short duration = (short) 126;
        Movie movie = new Movie(
                "The Power of the Dog",
                2021,
                duration,
                "United Kingdom",
                2000000,
                color);
        entityManager.persist(movie);
        assertAll(
                ()->assertNotNull(movie.getId()),
                ()->assertEquals("The Power of the Dog", movie.getTitle()),
                ()->assertEquals(2021, movie.getYear()),
                ()->assertEquals(duration, movie.getDuration())
        );
        System.out.println(movie);
    }

    @Test
    void testSaveTitleTooLong(){
        String title = RandomString.make(301);
        Movie movie = new Movie(title, 2021);
        assertThrows(PersistenceException.class,
                () -> entityManager.persist(movie));
    }

    @Rollback(false)
    @Test
    void testSaveMovieWithDirector(){
        Movie movie = new Movie("Django Unchained", 2012);
        Movie movie2 = new Movie("Pulp Fiction", 1994);
        Person person = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        entityManager.persist(movie);
        entityManager.persist(movie2);
        entityManager.persist(person);
        entityManager.flush();
        // set bidirectional associations
        movie.setDirector(person);
        movie2.setDirector(person);
        Collections.addAll(person.getDirectedMovies(), movie, movie2);
        entityManager.flush(); // to generate DML update x 2 now
        int idPerson = person.getId();
        int idMovie = movie.getId();
        // clear cache before reading data persisted from person point of view
        entityManager.clear();
        Person personRead = entityManager.find(Person.class, idPerson);
        System.out.println(personRead);
        System.out.println(personRead.getDirectedMovies());
        // clear cache before reading data persisted from movie point of view
        entityManager.clear();
        Movie movieRead = entityManager.find(Movie.class, idMovie);
        System.out.println(movieRead);
        System.out.println(movieRead.getDirector());
    }

    /**
     * this test is written in Java 11
     */
    @Rollback(false)
    @Test
    void testSaveMovieWithActors(){
        var moviePulp = new Movie("Pulp Fiction", 1994);
        var movieKB1 = new Movie("Kill Bill: Vol. 1", 2003);
        var movieKB2 = new Movie("Kill Bill: Vol. 2", 2004);
        var movies = List.of(moviePulp, movieKB1, movieKB2);
        var uma = new Person("Uma Thurman", LocalDate.of(1970, 4, 29));
        var castPulp = Map.of(
                new Person("John Travolta", LocalDate.of(1954,2,18)), "Vincent Vega",
                new Person("Samuel L. Jackson", LocalDate.of(1948, 12, 21)), "Jules Winnfield",
                new Person("Bruce Willis", LocalDate.of(1955,3,19)), "Butch Coolidge"
        );
        var filmoUma = Map.of(
                moviePulp, "Mia Wallace",
                movieKB1, "The Bride",
                movieKB2, "Beatrix Kiddo aka The Bride aka Black Mamba aka Mommy"
        );
        // persist movies
        movies.forEach(entityManager::persist);
        // persist actor/persons
        castPulp.keySet().forEach(entityManager::persist);
        entityManager.persist(uma);
        entityManager.flush(); // generate DML for movies and actors
        // associate objects (bidirectional)
        // - actors of Pulp Fiction
        castPulp.forEach((a, r) -> {
                    // play to (movie, actor/person)
                    var play = new Play(moviePulp, a, r);
                    // movie to play
                    moviePulp.getPlays().add(play);
                    // actor/person to play
                    a.getPlays().add(play);
                    entityManager.persist(play);
            });
        entityManager.flush(); // force SQL synchro : 3 insert into play
        // - other movies of Uma Thurman
        filmoUma.forEach((m,r) -> {
                    var play = new Play(m, uma, r);
                    m.getPlays().add(play);
                    uma.getPlays().add(play);
                    entityManager.persist(play);
                });
        entityManager.flush(); // force SQL synchro : 3 insert into play
        // remember some ids before cleaning cache
        var idMoviePulp = moviePulp.getId();
        var idUma = uma.getId();
        // clear cache
        // read cast of a movie
        entityManager.clear();
        var movieRead = entityManager.find(Movie.class, idMoviePulp);
        System.out.println(movieRead);
        movieRead.getPlays().forEach(pl -> System.out.println("\t - " + pl.getActor() + " as " + pl.getRole()));
        // clear cache again
        // read filmography as an actor
        entityManager.clear();
        var actorRead = entityManager.find(Person.class, idUma);
        System.out.println(actorRead);
        actorRead.getPlays().forEach(pl -> System.out.println("\t * " + pl.getMovie() + " as " + pl.getRole()));
    }


}