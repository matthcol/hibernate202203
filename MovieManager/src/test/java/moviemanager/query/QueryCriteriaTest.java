package moviemanager.query;

import moviemanager.entity.Movie;
import moviemanager.entity.Movie_;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;
import javax.sql.rowset.spi.SyncProvider;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueryCriteriaTest {

    @Autowired
    EntityManager entityManager;

    /**
     * generation du meta model :
     * https://docs.jboss.org/hibernate/orm/5.6/topical/html_single/metamodelgen/MetamodelGenerator.html
     */
    @Test
    void testMovieByTitle() {
        var title = "The Man Who Knew Too Much";
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var criteriaMovie = criteriaBuilder.createQuery(Movie.class);
        var root = criteriaMovie.from(Movie.class);
        criteriaMovie.select(root);
        criteriaMovie.where(
                criteriaBuilder.equal(root.get(Movie_.title), title)
        );
        entityManager.createQuery(criteriaMovie)
                .getResultStream()
                .limit(10)
                .forEach(System.out::println);

    }
}
