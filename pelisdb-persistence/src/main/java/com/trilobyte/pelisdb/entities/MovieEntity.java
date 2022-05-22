package com.trilobyte.pelisdb.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "movies")
@NoArgsConstructor
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 250, nullable = false)
    private String title;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "genre", length = 50, nullable = false)
    private String genre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movies_actors", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private List<ActorEntity> actors;

    @Column(name = "poster", length = 350, nullable = false)
    private String poster;

    @ElementCollection
    @CollectionTable(name = "movies_languages", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "language")
    private List<String> languages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<ActorEntity> getActors() {
        return actors;
    }

    public void setActors(List<ActorEntity> actors) {
        this.actors = actors;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return "MovieEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", genre='" + genre + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieEntity that = (MovieEntity) o;
        return id.equals(that.id) && title.equals(that.title) && year.equals(that.year) && genre.equals(that.genre) && Objects.equals(poster, that.poster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, year, genre, poster);
    }
}
