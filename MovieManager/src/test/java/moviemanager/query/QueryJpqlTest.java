package moviemanager.query;

import moviemanager.dto.MovieStat;
import moviemanager.entity.Movie;
import moviemanager.entity.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.stream.Collectors;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueryJpqlTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testMoviesByTitleAsList() {
        var res =entityManager.createQuery(
                "select m from Movie m where title = :title",
                Movie.class)
                .setParameter("title", "The Man Who Knew Too Much")
                .getResultList();
        for (var movie: res){
            System.out.println(movie.getTitle() + "(" + movie.getYear() + ")");
        }
    }

    @Test
    void testMoviesByYearAsStream() {
        entityManager.createQuery(
                        "select m from Movie m where year = :year",
                        Movie.class)
                .setParameter("year", 1994)
                .getResultStream()
                .filter(m -> ! m.getTitle().equals("True Lies"))
                .forEach(movie ->
                    System.out.println(movie.getTitle() + "(" + movie.getYear() + ")")
                );
    }

    @Test
    void testCountMovies() {
        long res = entityManager.createQuery(
                "select count(m) from Movie m", Long.class)
                .getSingleResult();
        System.out.println("Count movies: " +  res);
    }

    @Test
    void testMoviesByYearWithDirector(){
        entityManager.createQuery(
                        "select m from Movie m left join fetch m.director where year = :year",
                        Movie.class)
                .setParameter("year", 1994)
                .getResultStream()
                .forEach(movie ->
                        System.out.println(movie.getTitle()
                                + " (" + movie.getYear() + ")"
                                + " by " + movie.getDirector()
                        ));
    }

    @Test
    void testPersonByBirthYear(){
        entityManager.createQuery(
                "select p from Person p where YEAR(birthdate) = :year",
                Person.class)
            .setParameter("year", 1930)
            .getResultStream()
            .forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings={"Clint Eastwood", "Quentin Tarantino"})
    void testMoviesByDirector(String directorName){
        entityManager.createQuery(
                    "select m from Movie m join fetch m.director d where d.name = :name",
                    Movie.class)
                .setParameter("name",directorName)
                .getResultStream()
                .forEach(movie ->
                        System.out.println(
                                "\t - "
                                + movie.getTitle()
                                + " (" + movie.getYear() + ")"
                                + " by " + movie.getDirector()
                        ));
    }

    @ParameterizedTest
    @ValueSource(strings={"Clint Eastwood", "Quentin Tarantino"})
    void testMoviesByActor(String directorName){
        entityManager.createQuery(
                        "select m from Movie m join m.actors c join fetch m.actors a where c.name = :name",
                        Movie.class)
                .setParameter("name",directorName)
                .getResultStream()
                .forEach(movie ->
                        System.out.println(
                                "\t - "
                                        + movie.getTitle()
                                        + " (" + movie.getYear() + ")"
                                        + " with "
                                        + movie.getActors()  // only clint here
                                        .stream()
                                        //.limit(5)
                                        .map(Person::getName)
                                        .collect(Collectors.joining(", "))
                        ));
    }

    @Test
    void testMoviesByEntityDirector(){
        var person = entityManager.find(Person.class, 142);
        entityManager.createQuery(
                        "select m from Movie m join fetch m.director d where d.name = :name",
                        Movie.class)
                .setParameter("name", person.getName())
                .getResultStream()
                .forEach(movie ->
                        System.out.println(
                                "\t - "
                                        + movie.getTitle()
                                        + " (" + movie.getYear() + ")"
                                        + " by " + movie.getDirector()
                        ));
    }


    @Test
    void testStatsTuple(){
        entityManager.createQuery(
                "select year as year, count(m) as count_movie from Movie m group by year",
                // Object[].class
                Tuple.class)
                .getResultStream()
                .forEach(t ->
                        System.out.println(
                                "\t - "
                                + t.get("year", Integer.class)
                                + " : "
                                + t.get("count_movie", Long.class)
                        ));
    }

    @Test
    void testStatsDTO(){
        entityManager.createQuery(
                        "select new moviemanager.dto.MovieStat(year, count(m)) from Movie m group by year",
                        MovieStat.class)
                .getResultStream()
                .forEach(stat ->
                        System.out.println(
                                "\t - "
                                        + stat.getYear()
                                        + " : "
                                        + stat.getCountMovie()
                        ));
    }

    @Test
    void testFindByDirectorBorn() {
        int year = 1930;
        entityManager.createNamedQuery("Movie.findByDirectorBorn", Movie.class)
                .setParameter("year", 1930)
                .getResultStream()
                .forEach(m -> System.out.println("\t -" + m));
    }
}
