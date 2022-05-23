package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.ActorDto;
import com.trilobyte.pelisdb.dto.GenreDto;
import com.trilobyte.pelisdb.dto.LanguageDto;
import com.trilobyte.pelisdb.dto.MovieDto;
import com.trilobyte.pelisdb.dto.MovieReqDto;
import com.trilobyte.pelisdb.entities.ActorEntity;
import com.trilobyte.pelisdb.entities.MovieEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MovieMappersTests.class})
public class MovieMappersTests {

    @InjectMocks
    private MovieMappers mapper = new MovieMappersImpl();

    @Mock
    private ActorMapper actorMapper;

    @Mock
    private GenreMapper genreMapper;

    @Test
    void toEntity_returnsEntity_fromValidMovieDto() {
        // GIVEN
        final String title = "anyTitle";
        final Integer year = 1;
        final String poster = "anyPoster";
        final String genre = "Action";
        final String language = "Spanish";
        final Long actorId = 1L;
        final String actor = "anyActor";
        final ActorDto actorDto = new ActorDto();
        actorDto.id(actorId);
        actorDto.setName(actor);
        final MovieDto dto = new MovieDto();
        dto.setTitle(title);
        dto.setYear(year);
        dto.setPoster(poster);
        dto.setGenre(GenreDto.fromValue(genre));
        dto.setLanguages(Arrays.asList(LanguageDto.fromValue(language)));
        dto.setActors(Arrays.asList(actorDto));
        final ActorEntity actorEntity = new ActorEntity();
        actorEntity.setId(actorId);
        actorEntity.setName(actor);
        given(actorMapper.toEntity(any( ActorDto.class))).willReturn(actorEntity);

        // WHEN
        final MovieEntity response = mapper.toEntity(dto);

        // THEN
        then(actorMapper).should(times(1)).toEntity(any(ActorDto.class));
        assertThat(response).isNotNull();
        assertEquals(title, response.getTitle());
        assertEquals(year, response.getYear());
        assertEquals(poster, response.getPoster());
        assertEquals(genre.toUpperCase(), response.getGenre());
        assertThat(response.getLanguages()).isNotNull();
        assertEquals(1, response.getLanguages().size());
        assertEquals(language.toUpperCase(), response.getLanguages().get(0));
        assertThat(response.getActors()).isNotNull();
        assertEquals(1, response.getActors().size());
        assertEquals(actor, response.getActors().get(0).getName());
        assertEquals(actorId, response.getActors().get(0).getId());
    }

    @Test
    void toEntity_returnNull_fromNullMovieDto() {
        // GIVEN

        // WHEN
        final MovieEntity response = mapper.toEntity(null);

        // THEN
        assertThat(response).isNull();
    }

    @Test
    void toDto_returnMovieDto_fromValidEntity() {
        // GIVEN
        final String title = "anyTitle";
        final Integer year = 1;
        final String poster = "anyPoster";
        final String genre = "anyGenre";
        final String language = "SPANISH";
        final Long actorId = 1L;
        final String actor = "anyActor";
        final MovieEntity entity = new MovieEntity();
        entity.setTitle(title);
        entity.setYear(year);
        entity.setPoster(poster);
        entity.setGenre(genre);
        entity.setLanguages(Arrays.asList(language));
        entity.setActors(Arrays.asList(new ActorEntity()));
        final ActorDto actorDto = new ActorDto();
        actorDto.setId(actorId);
        actorDto.setName(actor);
        given(actorMapper.toDto(any(ActorEntity.class))).willReturn(actorDto);
        given(genreMapper.toDto(anyString())).willReturn(GenreDto.OTHER);

        // WHEN
        final MovieDto response = mapper.toDto(entity);

        // THEN
        then(actorMapper).should(times(1)).toDto(any(ActorEntity.class));
        then(genreMapper).should(times(1)).toDto(any(String.class));
        assertThat(response).isNotNull();
        assertEquals(title, response.getTitle());
        assertEquals(year, response.getYear());
        assertEquals(poster, response.getPoster());
        assertEquals("Other", response.getGenre().getValue());
        assertThat(response.getLanguages()).isNotNull();
        assertEquals(1, response.getLanguages().size());
        assertEquals("Spanish", response.getLanguages().get(0).getValue());
        assertThat(response.getActors()).isNotNull();
        assertEquals(1, response.getActors().size());
        assertEquals(actor, response.getActors().get(0).getName());
        assertEquals(actorId, response.getActors().get(0).getId());
    }

    @Test
    void toDto_returnMovieDto_fromNotValidEntity() {
        // GIVEN
        final MovieEntity entity = new MovieEntity();
        final ActorDto actorDto = new ActorDto();
        given(actorMapper.toDto(any(ActorEntity.class))).willReturn(null);
        given(genreMapper.toDto(anyString())).willReturn(null);

        // WHEN
        final MovieDto response = mapper.toDto(entity);

        // THEN
        then(actorMapper).should(times(0)).toDto(any(ActorEntity.class));
        then(genreMapper).should(times(0)).toDto(anyString());
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isNull();
        assertThat(response.getYear()).isNull();
        assertThat(response.getPoster()).isNull();
        assertThat(response.getActors()).isNotNull();
        assertThat(response.getGenre()).isNull();
        assertThat(response.getLanguages()).isNotNull();
    }

    @Test
    void toDto_returnMovieDtoList_fromValidEntityList() {
        // GIVEN
        final String title = "anyTitle";
        final Integer year = 1;
        final String poster = "anyPoster";
        final String genre = "anyGenre";
        final String language = "SPANISH";
        final Long actorId = 1L;
        final String actor = "anyActor";
        final MovieEntity entity = new MovieEntity();
        entity.setTitle(title);
        entity.setYear(year);
        entity.setPoster(poster);
        entity.setGenre(genre);
        entity.setLanguages(Arrays.asList(language));
        entity.setActors(Arrays.asList(new ActorEntity()));
        final ActorDto actorDto = new ActorDto();
        actorDto.setId(actorId);
        actorDto.setName(actor);
        final MovieEntity entity2 = new MovieEntity();
        entity2.setTitle(title+2);
        entity2.setYear(year+2);
        entity2.setPoster(poster+2);
        entity2.setGenre(genre);
        entity2.setLanguages(Arrays.asList(language));
        entity2.setActors(Arrays.asList(new ActorEntity()));
        given(actorMapper.toDto(any(ActorEntity.class))).willReturn(actorDto);
        given(genreMapper.toDto(anyString())).willReturn(GenreDto.OTHER);

        // WHEN
        final List<MovieDto> response = mapper.toDto(Arrays.asList(entity, entity2));

        // THEN
        then(actorMapper).should(times(2)).toDto(any(ActorEntity.class));
        then(genreMapper).should(times(2)).toDto(any(String.class));
        assertThat(response).isNotNull();
        assertEquals(2, response.size());
        assertEquals(title, response.get(0).getTitle());
        assertEquals(year, response.get(0).getYear());
        assertEquals(poster, response.get(0).getPoster());
        assertEquals("Other", response.get(0).getGenre().getValue());
        assertThat(response.get(0).getLanguages()).isNotNull();
        assertEquals(1, response.get(0).getLanguages().size());
        assertEquals("Spanish", response.get(0).getLanguages().get(0).getValue());
        assertThat(response.get(0).getActors()).isNotNull();
        assertEquals(1, response.get(0).getActors().size());
        assertEquals(actor, response.get(0).getActors().get(0).getName());
        assertEquals(actorId, response.get(0).getActors().get(0).getId());

        assertEquals(title+2, response.get(1).getTitle());
        assertEquals(year+2, response.get(1).getYear());
        assertEquals(poster+2, response.get(1).getPoster());
        assertEquals("Other", response.get(1).getGenre().getValue());
        assertThat(response.get(1).getLanguages()).isNotNull();
        assertEquals(1, response.get(1).getLanguages().size());
        assertEquals("Spanish", response.get(1).getLanguages().get(0).getValue());
        assertThat(response.get(1).getActors()).isNotNull();
        assertEquals(1, response.get(1).getActors().size());
        assertEquals(actor, response.get(1).getActors().get(0).getName());
        assertEquals(actorId, response.get(1).getActors().get(0).getId());
    }

    @Test
    void toDto_returnMovieDto_fromValidReqDto() {
        // GIVEN
        final String title = "anyTitle";
        final Integer year = 1;
        final String poster = "anyPoster";
        final String genre = "OTHER";
        final String language = "Spanish";
        final Long actorId = 1L;
        final String actor = "anyActor";
        final MovieReqDto reqDto = new MovieReqDto();
        reqDto.setTitle(title);
        reqDto.setYear(year);
        reqDto.setPoster(poster);
        reqDto.setGenre(GenreDto.valueOf(genre));
        reqDto.setLanguages(Arrays.asList(LanguageDto.fromValue(language)));
        reqDto.setActors(Arrays.asList(new ActorDto()));
        final ActorDto actorDto = new ActorDto();
        actorDto.setId(actorId);
        actorDto.setName(actor);
        reqDto.setActors(Arrays.asList(actorDto));
        given(genreMapper.toDto(anyString())).willReturn(GenreDto.OTHER);

        // WHEN
        final MovieDto response = mapper.toDto(reqDto);

        // THEN
        then(actorMapper).should(times(0)).toDto(any(ActorEntity.class));
        then(genreMapper).should(times(0)).toDto(any(String.class));
        assertThat(response).isNotNull();
        assertEquals(title, response.getTitle());
        assertEquals(year, response.getYear());
        assertEquals(poster, response.getPoster());
        assertEquals("Other", response.getGenre().getValue());
        assertThat(response.getLanguages()).isNotNull();
        assertEquals(1, response.getLanguages().size());
        assertEquals("Spanish", response.getLanguages().get(0).getValue());
        assertThat(response.getActors()).isNotNull();
        assertEquals(1, response.getActors().size());
        assertEquals(actor, response.getActors().get(0).getName());
        assertEquals(actorId, response.getActors().get(0).getId());
    }
}
