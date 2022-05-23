package com.trilobyte.pelisdb.services;

import com.trilobyte.pelisdb.dto.ActorDto;
import com.trilobyte.pelisdb.dto.MovieDto;
import com.trilobyte.pelisdb.dto.MovieReqDto;
import com.trilobyte.pelisdb.dto.MovieResDto;
import com.trilobyte.pelisdb.entities.ActorEntity;
import com.trilobyte.pelisdb.entities.MovieEntity;
import com.trilobyte.pelisdb.exceptions.MovieNotFoundException;
import com.trilobyte.pelisdb.mappers.ActorMapper;
import com.trilobyte.pelisdb.mappers.MovieMappers;
import com.trilobyte.pelisdb.repository.ActorsRepository;
import com.trilobyte.pelisdb.repository.MoviesRepository;
import com.trilobyte.pelisdb.services.impl.MovieServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MovieServiceTest.class})
public class MovieServiceTest {

    @Mock
    private MoviesRepository moviesRepository;

    @Mock
    private ActorsRepository actorsRepository;

    @Spy
    private MovieMappers movieMappers;

    @Spy
    private ActorMapper actorMapper;

    @InjectMocks
    private MovieService movieService = new MovieServiceImpl();

    @Test
    void findAll_WithTitle_returnEmptyList_whenNoMovieAdded() {
        // GIVEN
        final String title = "anyTitle";
        final Page<MovieEntity> moviePage = new PageImpl<>(Collections.emptyList() , PageRequest.of(1, 1), 1);
        given(moviesRepository.findAllByTitleContainingIgnoreCase(anyString(), any(Pageable.class))).willReturn(moviePage);
        given(movieMappers.toDto(anyList())).willReturn(Collections.emptyList());

        // WHEN
        final MovieResDto response = movieService.findAll(title, 1, 1, null, null);

        // THEN
        then(moviesRepository).should().findAllByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
        then(movieMappers).should().toDto(anyList());
        assertThat(response).isNotNull();
        assertEquals(0, response.getMovies().size());
    }

    @Test
    void findAll_WithTitle_ReturnsAMovie_whenMovieMatches() {
        // GIVEN
        final String title = "anyTitle";
        final MovieDto movieDto = new MovieDto();
        movieDto.setTitle(title);
        final MovieEntity entity = new MovieEntity();
        final Page<MovieEntity> moviePage = new PageImpl<>(Arrays.asList(entity) , PageRequest.of(0, 10), 1);
        given(moviesRepository.findAllByTitleContainingIgnoreCase(anyString(), any(Pageable.class))).willReturn(moviePage);
        given(movieMappers.toDto(anyList())).willReturn(Arrays.asList(movieDto));

        // WHEN
        final MovieResDto response = movieService.findAll(title, 0, 10, null, null);

        // THEN
        then(moviesRepository).should().findAllByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
        then(movieMappers).should().toDto(anyList());
        assertThat(response).isNotNull();
        assertEquals(1, response.getMovies().size());
        assertEquals(title, response.getMovies().get(0).getTitle());
    }

    @Test
    void findAll_WithTitle_ReturnsMultipleMovie_whenMovieMatches() {
        // GIVEN
        final String title1 = "anyTitle1";
        final String title2 = "anyTitle2";
        final MovieDto movieDto1 = new MovieDto();
        movieDto1.setTitle(title1);
        final MovieDto movieDto2 = new MovieDto();
        movieDto2.setTitle(title2);
        final MovieEntity entity = new MovieEntity();
        final Page<MovieEntity> moviePage = new PageImpl<>(Arrays.asList(entity) , PageRequest.of(0, 10), 1);
        given(moviesRepository.findAllByTitleContainingIgnoreCase(anyString(), any(Pageable.class))).willReturn(moviePage);
        given(movieMappers.toDto(anyList())).willReturn(Arrays.asList(movieDto1, movieDto2));

        // WHEN
        final MovieResDto response = movieService.findAll(title1, 0, 10, null, null);

        // THEN
        then(moviesRepository).should().findAllByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
        then(movieMappers).should().toDto(anyList());
        assertThat(response).isNotNull();
        assertEquals(2, response.getMovies().size());
        assertEquals(title1, response.getMovies().get(0).getTitle());
        assertEquals(title2, response.getMovies().get(1).getTitle());
    }

    @Test
    void findById_returnsAMovie_whenIdIsPresent() {
        // GIVEN
        final String title = "anyTitle";
        final MovieDto movieDto = new MovieDto();
        final MovieEntity entity = new MovieEntity();
        entity.setTitle(title);
        final Optional<MovieEntity> optionalMovieEntity = Optional.of(entity);
        movieDto.setTitle(title);
        given(moviesRepository.findById(anyLong())).willReturn(optionalMovieEntity);
        given(movieMappers.toDto(any(MovieEntity.class))).willReturn(movieDto);

        // WHEN
        final MovieDto response = movieService.findById(1L);

        // THEN
        then(moviesRepository).should().findById(anyLong());
        then(movieMappers).should().toDto(any(MovieEntity.class));
        assertThat(response).isNotNull();
        assertEquals(title, response.getTitle());
    }

    @Test
    void findById_returnsNoMovie_whenIdIsNotPresent() {
        // GIVEN
        given(moviesRepository.findById(anyLong())).willReturn(Optional.empty());

        // WHEN
        final MovieNotFoundException thrown =
                assertThrows(
                        MovieNotFoundException.class,
                        () -> {
                            movieService.findById(1L);
                        });

        // THEN
        then(movieMappers).should(never()).toDto(any(MovieEntity.class));
        Assertions.assertEquals("{error.movie.notFound}", thrown.getMessage());
    }

    @Test
    void save_returnsMovie_whenMovieIsAdded(){
        // GIVEN
        final String title = "anyTitle";
        final MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle(title);
        final MovieDto movieDto = new MovieDto();
        movieDto.setTitle(title);
        final ActorDto actorDto = new ActorDto();
        final String name = "anyName";
        actorDto.setName(name);
        final ActorEntity actorEntity = new ActorEntity();
        actorEntity.setName(name);
        movieEntity.setActors(Arrays.asList(actorEntity));
        given(moviesRepository.save(movieEntity)).willReturn(movieEntity);
        given(actorsRepository.save(actorEntity)).willReturn(actorEntity);
        given(actorsRepository.findById(anyLong())).willReturn(Optional.of(actorEntity));
        given(movieMappers.toEntity(any(MovieDto.class))).willReturn(movieEntity);
        given(movieMappers.toDto(any(MovieEntity.class))).willReturn(movieDto);
        given(movieMappers.toDto(any(MovieReqDto.class))).willReturn(movieDto);
        given(actorMapper.toEntity(any(ActorDto.class))).willReturn(actorEntity);
        given(actorMapper.toDto(any(ActorEntity.class))).willReturn(actorDto);

        // WHEN
        final MovieDto response = movieService.save(new MovieReqDto());

        // THEN
        then(moviesRepository).should().save(any(MovieEntity.class));
        then(movieMappers).should(times(1)).toDto(any(MovieEntity.class));
        then(movieMappers).should().toEntity(any(MovieDto.class));
        then(movieMappers).should().toDto(any(MovieReqDto.class));
        assertThat(response).isNotNull();
        assertEquals(title, response.getTitle());
    }

    @Test
    void update_returnsMovie_whenMovieExists(){
        // GIVEN
        final Long movieId = 1L;
        final String title = "anyTitle";
        final MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle(title);
        final MovieDto movieDto = new MovieDto();
        movieDto.setTitle(title);
        final ActorDto actorDto = new ActorDto();
        final String name = "anyName";
        actorDto.setName(name);
        final ActorEntity actorEntity = new ActorEntity();
        actorEntity.setName(name);
        movieEntity.setActors(Arrays.asList(actorEntity));
        given(moviesRepository.save(movieEntity)).willReturn(movieEntity);
        given(moviesRepository.existsById(anyLong())).willReturn(true);
        given(actorsRepository.save(actorEntity)).willReturn(actorEntity);
        given(actorsRepository.findById(anyLong())).willReturn(Optional.of(actorEntity));
        given(movieMappers.toEntity(any(MovieDto.class))).willReturn(movieEntity);
        given(movieMappers.toDto(any(MovieEntity.class))).willReturn(movieDto);
        given(movieMappers.toDto(any(MovieReqDto.class))).willReturn(movieDto);
        given(actorMapper.toEntity(any(ActorDto.class))).willReturn(actorEntity);
        given(actorMapper.toDto(any(ActorEntity.class))).willReturn(actorDto);

        // WHEN
        final MovieDto response = movieService.update(movieId, new MovieReqDto());

        // THEN
        then(moviesRepository).should().save(any(MovieEntity.class));
        then(movieMappers).should(times(1)).toDto(any(MovieEntity.class));
        then(movieMappers).should().toEntity(any(MovieDto.class));
        then(movieMappers).should().toDto(any(MovieReqDto.class));
        assertThat(response).isNotNull();
        assertEquals(title, response.getTitle());
    }

    @Test
    void update_throwsException_whenIdIsNotPresent() {
        // GIVEN
        final Long movieId = 1L;
        final String title = "anyTitle";
        final MovieReqDto movieReqDto = new MovieReqDto();
        movieReqDto.setTitle(title);
        given(moviesRepository.existsById(anyLong())).willReturn(false);

        // WHEN
        final MovieNotFoundException thrown =
                assertThrows(
                        MovieNotFoundException.class,
                        () -> {
                            movieService.update(movieId, movieReqDto);
                        });

        // THEN
        then(movieMappers).should(never()).toDto(anyList());
        then(moviesRepository).should(never()).save(any(MovieEntity.class));
        Assertions.assertEquals("Id not found", thrown.getMessage());
    }

    @Test
    void delete_returnsTrue_whenSuccessDeletion() {
        // Given
        final Long movieId = 1L;
        final MovieEntity entity = new MovieEntity();
        given(moviesRepository.findById(anyLong())).willReturn(Optional.of(entity));

        // When
        assertDoesNotThrow(() -> movieService.delete(movieId));

        // Then
        then(moviesRepository).should().delete(any(MovieEntity.class));
    }

    @Test
    void delete_returnsFalse_whenIdIsNotFound() {
        // Given
        final Long movieId = 1L;
        given(moviesRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        assertThrows(MovieNotFoundException.class, () -> movieService.delete(movieId));

        // Then
        then(moviesRepository).should(never()).delete(any(MovieEntity.class));
    }

 }
