package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.GenreDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GenreMapperTest.class})
public class GenreMapperTest {

    @InjectMocks
    private GenreMapper mapper = new GenreMapperImpl();

    @Test
    void toDto_returns_GenreDto_fromValidGenreStr() {
        // GIVEN
        final String genre = "COMEDY";

        // WHEN
        final GenreDto response = mapper.toDto(genre);

        // THEN
        assertThat(response).isNotNull();
        assertEquals(genre, response.name());
    }

    @Test
    void toDto_returns_OtherGenreDto_fromNoValidGenreStr() {
        // GIVEN
        final String genre = "anyGenre";

        // WHEN
        final GenreDto response = mapper.toDto(genre);

        // THEN
        assertThat(response).isNotNull();
        assertEquals("OTHER", response.name());
    }

}
