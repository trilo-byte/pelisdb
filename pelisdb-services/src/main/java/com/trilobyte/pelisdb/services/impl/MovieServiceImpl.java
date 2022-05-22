package com.trilobyte.pelisdb.services.impl;

import com.trilobyte.pelisdb.dto.MovieDto;
import com.trilobyte.pelisdb.dto.MovieReqDto;
import com.trilobyte.pelisdb.dto.MovieResDto;
import com.trilobyte.pelisdb.entities.MovieEntity;
import com.trilobyte.pelisdb.exceptions.MovieNotFoundException;
import com.trilobyte.pelisdb.mappers.ActorMapper;
import com.trilobyte.pelisdb.mappers.MovieMappers;
import com.trilobyte.pelisdb.repository.ActorsRepository;
import com.trilobyte.pelisdb.repository.MoviesRepository;
import com.trilobyte.pelisdb.services.MovieService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private static final String MOVIES_BY_ID = "movies_by_id";
    private static final String MOVIES_BY_TITLE = "movies_by_tilte";

    @Autowired
    private MoviesRepository moviesRepository;

    @Autowired
    private ActorsRepository actorsRepository;

    @Autowired
    private MovieMappers movieMappers;

    @Autowired
    private ActorMapper actorMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = MOVIES_BY_TITLE, key = "#title")
    public MovieResDto findAll(String title, Integer page, Integer size, String sort1, String sort2) {
        List<MovieEntity> movies = new ArrayList<>();
        Pageable paging = PageRequest.of(page, size);

        if (StringUtils.isNotBlank(sort1) && StringUtils.isNotBlank(sort2)) {
            paging = PageRequest.of(page, size, Sort.by(sort1).and(Sort.by(sort2)));
        } else if(StringUtils.isNotBlank(sort1) && StringUtils.isBlank(sort2)) {
            paging = PageRequest.of(page, size, Sort.by(sort1));
        } else if (StringUtils.isBlank(sort1) && StringUtils.isNotBlank(sort2)) {
            paging = PageRequest.of(page, size, Sort.by(sort2));
        }

        Page<MovieEntity> pageMovies;
        if (StringUtils.isBlank(title)) {
            pageMovies = moviesRepository.findAll(paging);
        } else {
            pageMovies = moviesRepository.findAllByTitleContainingIgnoreCase(title, paging);
        }

        movies = pageMovies.getContent();
        MovieResDto result = new MovieResDto();
        result.setMovies(movieMappers.toDto(movies));
        result.setCurrentPage(pageMovies.getNumber());
        result.setTotalItems(pageMovies.getTotalElements());
        result.setTotalPages(pageMovies.getTotalPages());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = MOVIES_BY_ID, key = "#id")
    public MovieDto findById(Long id) {
        final MovieEntity result = moviesRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("{error.movie.notFound}", id));
        return movieMappers.toDto(result);
    }

    @Override
    @Transactional
    @CachePut(value = MOVIES_BY_ID, key = "#result.title")
    @CacheEvict(value = MOVIES_BY_TITLE, allEntries = true)
    public MovieDto update(Long movieId, MovieReqDto dto) {
        if(!moviesRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Id not found");
        }
        final MovieEntity entity = movieMappers.toEntity(movieMappers.toDto(dto));
        entity.setId(movieId);
        // If one actor is not present on DB, it will be saved to prevent an error
        entity.setActors(entity.getActors().stream().map(
                actorEntity -> actorsRepository.findById(actorEntity.getId()).orElse(
                        actorsRepository.save(actorEntity)
                )
        ).collect(Collectors.toList()));
        final MovieEntity result = moviesRepository.save(entity);
        return movieMappers.toDto(result);
    }

    @Override
    @Transactional
    @CachePut(value = MOVIES_BY_ID, key = "#result.title")
    @CacheEvict(value = MOVIES_BY_TITLE, allEntries = true)
    public MovieDto save(MovieReqDto dto) {
        final MovieEntity entity = movieMappers.toEntity(movieMappers.toDto(dto));
        // If one actor is not present on DB, it will be saved to prevent an error
        entity.setActors(entity.getActors().stream().map(
                actorEntity -> actorsRepository.findById(actorEntity.getId()).orElse(
                        actorsRepository.save(actorEntity)
                )
        ).collect(Collectors.toList()));
        final MovieEntity result  = moviesRepository.save(entity);
        return movieMappers.toDto(result);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = MOVIES_BY_ID, key = "#id"),
                    @CacheEvict(value = MOVIES_BY_TITLE, allEntries = true)
            })
    public void delete(Long id) {
        final Optional<MovieEntity> entity = moviesRepository.findById(id);
        if(entity.isEmpty()) {
            throw new MovieNotFoundException("{error.movie.notFound}", id);
        }
        moviesRepository.delete(entity.get());
    }
}
