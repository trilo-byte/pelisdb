package com.trilobyte.pelisdb.repository;

import com.trilobyte.pelisdb.entities.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MoviesRepository extends PagingAndSortingRepository<MovieEntity, Long> {

    Page<MovieEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
}