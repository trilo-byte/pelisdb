package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.MovieDto;
import com.trilobyte.pelisdb.dto.MovieReqDto;
import com.trilobyte.pelisdb.entities.MovieEntity;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, uses = {ActorMapper.class, GenreMapper.class, LanguageMapper.class})
public interface MovieMappers {

    MovieEntity toEntity(MovieDto dto);

    MovieDto toDto(MovieEntity movie);

    List<MovieDto> toDto(List<MovieEntity> movies);

    @Mapping(target = "id", ignore = true)
    MovieDto toDto(MovieReqDto request);
}
