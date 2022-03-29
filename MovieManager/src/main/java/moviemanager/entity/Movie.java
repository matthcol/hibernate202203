package moviemanager.entity;

import javax.persistence.*;

@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 300, nullable = false)
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

    // mandatory if at least another constructor
    public Movie(){

    }

    public Movie(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    public Movie(String title, Integer year, Short duration, String countryOrigin, Integer budget, Color color) {
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

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                ", countryOrigin='" + countryOrigin + '\'' +
                '}';
    }
}
