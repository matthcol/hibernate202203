package moviemanager.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  id;
    private String name;

    // @Temporal(TemporalType.DATE) // DO NOT USE HERE
    // @Temporal should only be set on a java.util.Date or java.util.Calendar property
    private LocalDate birthdate;

    // association already defined in entity Movie on attribute director
    @OneToMany(mappedBy = "director")
    private Set<Movie> directedMovies = new HashSet<>();

    @OneToMany(mappedBy = "actor")
    private Set<Play> plays = new HashSet<>();

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, LocalDate birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Set<Movie> getDirectedMovies() {
        return directedMovies;
    }

    public void setDirectedMovies(Set<Movie> directedMovies) {
        this.directedMovies = directedMovies;
    }

    public Set<Play> getPlays() {
        return plays;
    }

    public void setPlays(Set<Play> plays) {
        this.plays = plays;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }
}
