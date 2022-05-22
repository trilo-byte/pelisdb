package com.trilobyte.pelisdb.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "actors")
@NoArgsConstructor
public class ActorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "actors")
    private List<MovieEntity> movies;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MovieEntity> getMovies() {
        return movies;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMovies(List<MovieEntity> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "ActorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActorEntity that = (ActorEntity) o;
        return id.equals(that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
