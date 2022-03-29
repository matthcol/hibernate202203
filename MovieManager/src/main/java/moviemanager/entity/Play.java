package moviemanager.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="play", uniqueConstraints =
    @UniqueConstraint(columnNames = {"fk_movie_id", "fk_actor_id", "role"}))
public class Play {

    // simplest way : add a simple id (instead of composite id with movie.getId, actor.getId (and role)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="fk_movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name="fk_actor_id")
    private Person actor;

    @Column(nullable = false)
    private String role;

    public Play(){}

    public Play(Movie movie, Person actor, String role) {
        this.movie = movie;
        this.actor = actor;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Person getActor() {
        return actor;
    }

    public void setActor(Person actor) {
        this.actor = actor;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
