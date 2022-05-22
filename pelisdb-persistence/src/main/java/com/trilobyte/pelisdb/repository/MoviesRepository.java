package com.trilobyte.pelisdb.repository;

import com.trilobyte.pelisdb.entities.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

//public interface MoviesRepository extends JpaRepository<MovieEntity, Long>, JpaSpecificationExecutor<MovieEntity> {
//
//    public static Specification<MovieEntity> titleContains(final String title) {
//        if (StringUtils.hasText(title)) {
//            return (movie, query, criteriaBuilder) ->
//                    criteriaBuilder.like(criteriaBuilder.lower(movie.get(MovieEntity_.TITLE)), new StringBuilder("%")
//                            .append(title.toLowerCase()).append("%").toString());
//        }
//        return null;
//    }
//}


public interface MoviesRepository extends PagingAndSortingRepository<MovieEntity, Long> {

    Page<MovieEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
}