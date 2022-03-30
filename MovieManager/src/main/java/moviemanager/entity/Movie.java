package moviemanager.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "movie", uniqueConstraints = @UniqueConstraint(columnNames = {"title", "year"}))
// @DynamicUpdate // Hibernate (not JPA) annotation to update only what's necessary
@NamedQuery(name="Movie.findByDirectorBorn",
        query = "select m from Movie m join m.director d where YEAR(d.birthdate) = :year")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 300, nullable = false) //  unique = true // this column unique
    private String title;  // obligatoire

    @Column(nullable = false)
    private Integer year;   // obligatoire

    @Column(nullable = true)
    private Short duration; // minutes, optionnel

    @Column(name="country_origin", nullable = true, length=150)
    private String countryOrigin;

    // no annotation => persistent
    // @Transient
    @Column(nullable = true)
    private Integer budget;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)  // STRING or ORDINAL (default)
    private Color color;

    // NB: for list you can add @OrderColumn
    @ElementCollection
    @CollectionTable(
            name = "genre",
            joinColumns = @JoinColumn(name = "fk_movie_id"))
    @Column(name="genre")
    private List<String> genres = new ArrayList<>(); // or initialize in constructor
    // private Set<String> genres;

    // @Transient // when mapping is not ready
    @ManyToOne(
            optional = true,
            fetch = FetchType.LAZY, // default Eager
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE }
    )
    @JoinColumn(name="fk_director_id", nullable = true)
    // @OnDelete(action = OnDeleteAction.CASCADE) // Hibernate Only not JPA
    private Person director;

    @ManyToMany // (fetch=FetchType.EAGER) // fetch Lazy default
    @JoinTable(
            name = "play",
            joinColumns = @JoinColumn(name="fk_movie_id"), // FK to this entity
            inverseJoinColumns = @JoinColumn(name="fk_actor_id") // FK to other entity
    )
    private Set<Person> actors = new HashSet<>();

    // mandatory if at least another constructor
    public Movie(){
        // genres = new ArrayList<>();
    }

    public Movie(String title, Integer year) {
        // this(); // to initialize collections
        this.title = title;
        this.year = year;
    }

    public Movie(String title, Integer year, Short duration, String countryOrigin, Integer budget, Color color) {
        // this(); // to initialize collections
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.countryOrigin = countryOrigin;
        this.budget = budget;
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Short getDuration() {
        return duration;
    }

    public void setDuration(Short duration) {
        this.duration = duration;
    }

    public String getCountryOrigin() {
        return countryOrigin;
    }

    public void setCountryOrigin(String countryOrigin) {
        this.countryOrigin = countryOrigin;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Person getDirector() {
        return director;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

    public Set<Person> getActors() {
        return actors;
    }

    public void setActors(Set<Person> actors) {
        this.actors = actors;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                ", countryOrigin='" + countryOrigin + '\'' +
                ", budget=" + budget +
                ", color=" + color +
                '}';
    }
}
