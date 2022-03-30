package moviemanager.query;

import moviemanager.entity.Movie;
import moviemanager.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.stream.Stream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueryMovieRepositoryTest {

    @Autowired
    MovieRepository movieRepository;

    // facts + check
    @Autowired
    TestEntityManager entityManager;

    // Spring repos have a lot of gifts : findById, findAll
    @Test
    void testFindById() {
        int id = 5257;
        var res = movieRepository.findById(id)
                .map(Movie::getTitle);
        res.ifPresentOrElse(
                t -> System.out.println("Movie found: " + t),
                () -> System.out.println("No movie with this id"));
    }

    @Test
    void testFindByExample(){
        var movie = new Movie();
        movie.setTitle("The Man Who Knew Too Much");
        var movieExample = Example.of(movie);
        var res = movieRepository.findAll(movieExample);
        res.forEach(System.out::println);
    }

    @Test
    void testFindByTitle() {
        String title = "The Man Who Knew Too Much";
        var res = movieRepository.findByTitle(title);
        System.out.println(res);
        Assertions.assertAll(res.stream().map(m -> () -> Assertions.assertEquals(title, m.getTitle())));
    }

    @ParameterizedTest
    @ValueSource(strings={"Clint Eastwood", "Quentin Tarantino"})
    void testFindByYearAndDirectorName(String directorName){
        var res = movieRepository.findByYearAndDirectorName(1992, directorName);
        res.forEach(m -> System.out.println("\t -" + m));
    }

    @ParameterizedTest
    @MethodSource("sortMovieSource")
    void testFindByYear(Sort sort) {
        var res = movieRepository.findByYear(1992, sort);
        res.forEach(m -> System.out.println("\t -" + m));
    }

    @Test
    void testFindByDirectorBorn() {
        int year = 1930;
        movieRepository.findByDirectorBorn(year)
            .forEach(m -> System.out.println("\t -" + m));
    }

    @Test
    void testFindByDirectorBorn2() {
        int year = 1930;
        movieRepository.findByDirectorBorn2(year)
                .forEach(m -> System.out.println("\t -" + m));
    }

    @Test
    void testAllStats(){
        movieRepository.allStats()
                .forEach(stat -> System.out.println("\t -"
                            + stat.getYear()
                            + " => count = "
                            + stat.getMovieCount()
                            + " ; total duration = "
                            + stat.getTotalDuration()
                ));
    }

    private static Stream<Sort> sortMovieSource(){
        return Stream.of(
                Sort.by("title"),
                Sort.by("duration", "title"),
                Sort.by(Sort.Order.desc("duration"), Sort.Order.asc("title"))
        );
    }
}
