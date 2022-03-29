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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

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

}