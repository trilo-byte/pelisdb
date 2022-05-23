package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.LanguageDto;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface LanguageMapper {

    default List<LanguageDto> toDto(final List<String> languages) {
        if(languages == null) {
            return null;
        }
        List<LanguageDto> response = new ArrayList<>();
        for(final LanguageDto dto: LanguageDto.values()) {
            if(languages.contains(dto.getValue())) {
                response.add(dto);
            }
        }
        return response;
    }
}
