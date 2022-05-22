package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.GenreDto;
import org.mapstruct.Mapper;

@Mapper
public interface GenreMapper {

    default GenreDto toDto(final String genre) {
        if (genre == null) {
            return null;
        }
        for(final GenreDto dto: GenreDto.values()) {
            if(genre.equalsIgnoreCase(dto.getValue())) {
                return dto;
            }
        }
        return GenreDto.OTHER;
    }
}
