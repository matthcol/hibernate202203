package moviemanager.dto;

public class MovieStat {

    private Integer year;
    private Long countMovie;

    public MovieStat(Integer year, Long countMovie) {
        this.year = year;
        this.countMovie = countMovie;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getCountMovie() {
        return countMovie;
    }

    public void setCountMovie(Long countMovie) {
        this.countMovie = countMovie;
    }
}
