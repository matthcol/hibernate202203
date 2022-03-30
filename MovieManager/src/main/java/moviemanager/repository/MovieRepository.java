package moviemanager.repository;

import moviemanager.dto.MovieAllStat;
import moviemanager.entity.Movie;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.NamedQuery;
import java.util.List;
import java.util.stream.Stream;

/**
 * vocabulary spring to generate SQL automatically :
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTitle(String title);
    Stream<Movie> findByYearAndDirectorName(Integer year, String directorName);
    Stream<Movie> findByYear(Integer year, Sort sort);

    Stream<Movie> findByDirectorBorn(Integer year);

    @Query("select m from Movie m join m.director d where YEAR(d.birthdate) = :year")
    Stream<Movie> findByDirectorBorn2(Integer year);

    @Query("select year as year, count(m) as movieCount, sum(duration) as totalDuration from Movie m group by year")
    Stream<MovieAllStat> allStats();
}
