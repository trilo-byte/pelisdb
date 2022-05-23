package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.LanguageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LanguageMapperTest.class})
public class LanguageMapperTest {

    @InjectMocks
    private LanguageMapper mapper = new LanguageMapperImpl();

    @Test
    void toDto_returnsLanguageDtoList_fromValidLanguageStringList() {
        // GIVEN
        final String english = "English";
        final String spanish = "Spanish";
        final List<String> languages = Arrays.asList(english, spanish);

        // WHEN
        final List<LanguageDto> response = mapper.toDto(languages);

        // THEN
        assertThat(response).isNotNull();
        assertEquals(2, response.size());
        assertEquals(english, response.get(0).getValue());
        assertEquals(spanish, response.get(1).getValue());
    }

    @Test
    void toDto_returnsEmptyList_fromNotValidLanguageStringList() {
        // GIVEN
        final String anyLanguage1 = "anyLanguage1";
        final String anyLanguage2 = "anyLanguage2";
        final List<String> languages = Arrays.asList(anyLanguage1, anyLanguage2);

        // WHEN
        final List<LanguageDto> response = mapper.toDto(languages);

        // THEN
        assertThat(response).isNotNull();
        assertEquals(0, response.size());
    }
}
