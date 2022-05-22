package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.LanguageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Named("LanguageMapper")
@Mapper
public interface LanguageMapper {

    @Named("toDto")
    default List<LanguageDto> toDto(final List<String> languages) {
        if(languages == null) {
            return null;
        }
        List<LanguageDto> response = new ArrayList<>();
        for(final LanguageDto dto: LanguageDto.values()) {
            if(languages.contains(dto)) {
                response.add(dto);
            }
        }
        return response;
    }
}
