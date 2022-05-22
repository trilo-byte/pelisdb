package com.trilobyte.pelisdb.controllers;

import com.trilobyte.pelisdb.aspect.annotation.Monitor;
import com.trilobyte.pelisdb.dto.MovieDto;
import com.trilobyte.pelisdb.dto.MovieReqDto;
import com.trilobyte.pelisdb.dto.MovieResDto;
import com.trilobyte.pelisdb.security.annotation.PreAuthAdmin;
import com.trilobyte.pelisdb.security.annotation.PreAuthUser;
import com.trilobyte.pelisdb.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@PreAuthUser
@Monitor
public class MoviesController implements MoviesApiDelegate {

    private final NativeWebRequest request;

    private final MovieService movieService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    @PreAuthAdmin
    public ResponseEntity<MovieDto> addMovie(MovieReqDto movieReqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.save(movieReqDto));
    }

    @Override
    @PreAuthAdmin
    public ResponseEntity<Void> deleteMovie(Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MovieDto> getMovie(Long id) {
        return ResponseEntity.ok(movieService.findById(id));
    }

    @Override
    public ResponseEntity<MovieResDto> getMovieByTitle(Integer page, Integer size, String title, String sort1, String sort2) {
        return ResponseEntity.ok(movieService.findAll(title, page, size, sort1, sort2));
    }

    @Override
    @PreAuthAdmin
    public ResponseEntity<MovieDto> updateMovie(Long id, MovieReqDto movieReqDto) {
        return ResponseEntity.ok(movieService.update(id, movieReqDto));
    }
}
