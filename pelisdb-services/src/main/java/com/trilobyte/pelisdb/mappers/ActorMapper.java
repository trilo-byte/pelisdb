package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.ActorDto;
import com.trilobyte.pelisdb.entities.ActorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ActorMapper {

    @Mapping(target = "movies", ignore = true)
    ActorEntity toEntity(ActorDto dto);

    ActorDto toDto(ActorEntity actor);

    List<ActorDto> toDtoList(List<ActorEntity> actors);
}
