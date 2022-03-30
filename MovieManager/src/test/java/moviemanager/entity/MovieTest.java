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
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("testu")
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

    @Test
    void testSaveMovieDoublesTitleYear(){
        var movie1 = new Movie("The Lion King", 1994);
        var movie2 = new Movie("The Lion King", 1994);
        entityManager.persist(movie1);
        assertThrows(PersistenceException.class,
                () -> entityManager.persist(movie2));
    }

    @Test
    void testSaveMovieSameTitleDifferentYear(){
        var movie1 = new Movie("The Man Who Knew Too Much", 1934);
        var movie2 = new Movie("The Man Who Knew Too Much", 1956);
        entityManager.persist(movie1);
        entityManager.persist(movie2);
    }

    @Test
    void testUpdateDirector(){
        var movie = new Movie("Sin City", 2005);
        var quentin = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        var robert = new Person("Robert Rodriguez", LocalDate.of(1968,6,20));
        var franck = new Person("Franck Miller", LocalDate.of(1957,1,27));
        var possibleDirectors = List.of(quentin, robert, franck);
        entityManager.persist(movie);
        possibleDirectors.forEach(entityManager::persist);
        entityManager.flush();
        for (var d : possibleDirectors) {
            movie.setDirector(d);
            entityManager.flush();
            // TODO : clear cache and read again movie with its director
        }
    }

    /**
     * play this test with :
     * - actors as a list : remove and rewrite all the actors
     * - actors as a set : remove or add only 1 actor
     * https://thorben-janssen.com/hibernate-tips-the-best-way-to-remove-entities-from-a-many-to-many-association/
     */
    @Test
    void testUpdateOneActorAmongSeveral(){
        var movie = new Movie("Pulp Fiction", 1994);
        var actors = List.of(
                new Person("John Travolta", LocalDate.of(1954,2,18)),
                new Person("Uma Thurman", LocalDate.of(1970, 4, 29)),
                new Person("Bruce Willis", LocalDate.of(1955,3,19))
        );
        var sam = new Person("Samuel L. Jackson", LocalDate.of(1900,1,1));
        // persist entities movie and persons
        entityManager.persist(movie);
        actors.forEach(entityManager::persist);
        entityManager.persist(sam);
        entityManager.flush();
        // add first batch of actors
        movie.getActors().addAll((actors));
        entityManager.flush();
        // add another actor
        movie.getActors().add(sam);
        entityManager.flush();
        // remove one actor
        movie.getActors().remove(sam);
        entityManager.flush();
    }

    /**
     * test settings cascade = CascadeType.PERSIST
     * on many-to-many association director
     */
    @Test
    void testSaveMovieWithDirectorCascade() {
        // create movie with its director
        Movie movie = new Movie("Django Unchained", 2012);
        Person person = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        movie.setDirector(person);
        // persist both by saving only movie
        entityManager.persist(movie); // not working by default, ok if cascade persist
        entityManager.flush();
        // add onother movie from this person
        var movie2 = new Movie("Pulp Fiction", 1994);
        movie2.setDirector(person);
        // persist new movie (not the director already there)
        entityManager.persist(movie2);
        entityManager.flush();
    }

    /**
     * test settings cascade = CascadeType.REMOVE
     * on many-to-many association director
     */
    @Test
    void testRemoveMovieWithDirectorCascade(){
        // create movie with its director
        Movie movie = new Movie("Django Unchained", 2012);
        Person person = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        movie.setDirector(person);
        // persist both by saving only movie
        entityManager.persist(movie);
        entityManager.flush();
        // delete movie
        entityManager.remove(movie);  // by cascade remove its director
        entityManager.flush();
    }

    // @Rollback(false)
    @Test
    void testRemoveDirectorCascadeOrNotCascade(){
        // create movie with its director
        Movie movie = new Movie("Django Unchained", 2012);
        Person person = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        movie.setDirector(person);
        // persist both by saving only movie
        entityManager.persist(movie);
        entityManager.flush();
        var idPerson = person.getId();
        entityManager.clear();
        // delete person :
        var personRead = entityManager.find(Person.class, idPerson);
        // - with on delete cascade set : movies from this person are removed to
        // but you don't see it in the SQL, it's done internally in the db
        // entityManager.remove(personRead);
        // entityManager.flush();  // OK both are removed
        // - with default settings : javax.persistence.PersistenceException: org.hibernate.exception.ConstraintViolationException: could not execute statement
        // person can't be removed because it's referenced by a movie
        assertThrows(PersistenceException.class, () -> {
                entityManager.remove(personRead);
                entityManager.flush();
        });
        // NB: delete movie if ondelete cascade set (by RDBMS directly not hibernate)

    }

    @Test
    void testRemoveDirectorSetNullProg(){
        // create movie with its director
        Movie movie = new Movie("Django Unchained", 2012);
        Person person = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        movie.setDirector(person);
        // persist both by saving only movie
        entityManager.persist(movie);
        entityManager.flush();
        // delete person with on delete set null (programmed)
        movie.setDirector(null);  // repeat on all movies directed by this person
        entityManager.remove(person);
        entityManager.flush();
    }

    /**
     * alt way of removing person and his movies as a director
     */
    @Test
    void testRemoveDirectorCascadeProg(){
        // create movie with its director
        Movie movie = new Movie("Django Unchained", 2012);
        Person person = new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27));
        movie.setDirector(person);
        // persist both by saving only movie
        entityManager.persist(movie);
        entityManager.flush();
        // delete person with on delete cascade (programmed)
        entityManager.remove(movie); // repeat for all movies from this director
        entityManager.remove(person);
        entityManager.flush();
    }


}