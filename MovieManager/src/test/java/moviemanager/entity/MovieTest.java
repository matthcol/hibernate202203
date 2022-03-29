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
import java.util.List;

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

    //@Rollback(false)
    @Test
    void testSaveMovieWithDirector(){
        Movie movie = new Movie("Django Unchained", 2012);
        Person person = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        entityManager.persist(movie);
        entityManager.persist(person);
        entityManager.flush();
        movie.setDirector(person);
        entityManager.flush(); // to generate DML update now
        int idMovie = movie.getId();
        // clear cache before reading data persisted
        entityManager.clear();
        Movie movieRead = entityManager.find(Movie.class, idMovie);
        System.out.println(movieRead);
        System.out.println(movieRead.getDirector());
    }


    /**
     * this test is written in Java 11
     */
    @Test
    void testSaveMovieWithActors(){
        var movie = new Movie("Pulp Fiction", 1994);
        var actors = List.of(
                new Person("John Travolta", LocalDate.of(1954,2,18)),
                new Person("Uma Thurman", LocalDate.of(1970, 4, 29)),
                new Person("Bruce Willis", LocalDate.of(1955,3,19))
        );
        entityManager.persist(movie);
        // for (var p: actors){
        //     entityManager.persist(p);
        // }
        // actors.forEach((Person p) -> entityManager.persist(p));
        // actors.forEach(p -> entityManager.persist(p));
        actors.forEach(entityManager::persist);
        entityManager.flush();
        movie.getActors().addAll(actors);
        entityManager.flush(); // force SQL synchro : 3 insert into play
        var idMovie = movie.getId();
        // clear cache
        entityManager.clear();
        var movieRead = entityManager.find(Movie.class, idMovie);
        System.out.println(movieRead);
        movieRead.getActors().forEach(a -> System.out.println("\t - " + a));
    }

}