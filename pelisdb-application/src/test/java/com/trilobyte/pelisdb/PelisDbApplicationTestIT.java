package com.trilobyte.pelisdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilobyte.pelisdb.dto.ActorDto;
import com.trilobyte.pelisdb.dto.GenreDto;
import com.trilobyte.pelisdb.dto.LanguageDto;
import com.trilobyte.pelisdb.dto.MovieReqDto;
import com.trilobyte.pelisdb.entities.ActorEntity;
import com.trilobyte.pelisdb.entities.MovieEntity;
import com.trilobyte.pelisdb.repository.ActorsRepository;
import com.trilobyte.pelisdb.repository.MoviesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = PelisDbApplication.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin@mock.es", password = "", roles = "ADMIN")
public class PelisDbApplicationTestIT {

        @Autowired
        private MockMvc mvc;

        @Autowired
        private MoviesRepository moviesRepository;

        @Autowired
        private ActorsRepository actorsRepository;

        @Test
        void givenAValidId_whenGetById_thenReturnsAMovie() throws Exception {
                final long id = 3L;
                final String title = "superman II";
                final String genre = "Comedy";
                final String poster = "http://www.freepic.com/anyPic.jpg";
                final int year = 2000;
                final String actor = "anyActor";
                final List<String> languages = Collections.singletonList("ENGLISH");
                final List<ActorEntity> actors = Arrays.asList(createActorEntity(actor));
                createMovieEntity(title, genre, poster, year, languages, actors);

                mvc.perform(get(new StringBuilder("/movies/").append(id).toString()).contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.title", is(title)))
                        .andExpect(jsonPath("$.genre", is(genre)))
                        .andExpect(jsonPath("$.languages[0]", is("English")))
                        .andExpect(jsonPath("$.year", is(year)))
                        .andExpect(jsonPath("$.actors[0].name", is(actor)));
        }

        @Test
        void givenAnInValidId_whenGetById_thenReturnsA404() throws Exception {
                final long id = 10L;

                mvc.perform(get(new StringBuilder("/movies/").append(id).toString()).contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound());
        }

        @Test
        void givenNoIdAndNoName_whenGetById_thenReturnsAMovie() throws Exception {
                final String title = "Me, Myself and Irene";
                final String genre = "Comedy";
                final int year = 2000;
                final String actor = "Jim Carrey";
                final List<String> languages = Collections.singletonList("ENGLISH");
                mvc.perform(get("/movies/?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.movies", hasSize(greaterThanOrEqualTo(1))))
                        .andExpect(jsonPath("$.movies[0].title", is(title)))
                        .andExpect(jsonPath("$.movies[0].genre", is(genre)))
                        .andExpect(jsonPath("$.movies[0].languages[0]", is("English")))
                        .andExpect(jsonPath("$.movies[0].year", is(year)))
                        .andExpect(jsonPath("$.movies[0].actors[0].name", is(actor)));
        }

        @Test
        void givenNoIdAndNoName_whenGetByIdWithNoPaging_thenReturns400() throws Exception {
                final String title = "Me, Myself and Irene";
                final String genre = "Comedy";
                final int year = 2000;
                final String actor = "Jim Carrey";
                final List<String> languages = Collections.singletonList("ENGLISH");
                mvc.perform(get("/movies/").contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isBadRequest());;
        }

        @Test
        void givenNoIdAndTitle_whenGetById_thenReturnsAMovie() throws Exception {
                final String title = "Me, Myself and Irene";
                final String genre = "Comedy";
                final int year = 2000;
                final String actor = "Jim Carrey";
                final List<String> languages = Collections.singletonList("ENGLISH");
                mvc.perform(get(new StringBuilder("/movies/?title=").append(title).append("&page=0&size=1").toString())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.movies", hasSize(greaterThanOrEqualTo(1))))
                        .andExpect(jsonPath("$.movies[0].title", is(title)))
                        .andExpect(jsonPath("$.movies[0].genre", is(genre)))
                        .andExpect(jsonPath("$.movies[0].languages[0]", is("English")))
                        .andExpect(jsonPath("$.movies[0].year", is(year)))
                        .andExpect(jsonPath("$.movies[0].actors[0].name", is(actor)));
        }

        @Test
        void givenNoIdAndNoValidTitle_whenGetById_thenReturnsNoMovie() throws Exception {
                final String title = "anyTitle";
                mvc.perform(get(new StringBuilder("/movies/?title=").append(title).append("&page=0&size=1").toString())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.movies", hasSize(equalTo(0))));
        }

        @Test
        void givenAValidMovie_whenAddMovie_thenReturnsAMovie() throws Exception {
                final String title = "Terminator";
                final String genre = "COMEDY";
                final String poster = "http://www.freepic.com/anyPic.jpg";
                final int year = 2000;
                final String actor = "anyActor";
                final List<String> languages = Collections.singletonList("English");
                final List<ActorDto> actors = Arrays.asList(createActorDto(actor));
                final MovieReqDto reqDto = createMovieReq(title, genre, poster, year, languages, actors);
                final ObjectMapper mapper = new ObjectMapper();
                final String reqDtoJson = mapper.writeValueAsString(reqDto);

                mvc.perform(post("/movies/").contentType(MediaType.APPLICATION_JSON).content(reqDtoJson))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.title", is(title)))
                        .andExpect(jsonPath("$.genre", is("Comedy")))
                        .andExpect(jsonPath("$.languages[0]", is("English")))
                        .andExpect(jsonPath("$.year", is(year)))
                        .andExpect(jsonPath("$.actors[0].name", is(actor)));
        }

        @Test
        void givenANoValidMovie_whenAddMovie_thenReturnsBadRequest() throws Exception {
                final String title = "";
                final String genre = "COMEDY";
                final String poster = "http://www.freepic.com/anyPic.jpg";
                final int year = 2000;
                final String actor = "anyActor";
                final List<String> languages = Collections.singletonList("English");
                final List<ActorDto> actors = Arrays.asList(createActorDto(actor));
                final MovieReqDto reqDto = createMovieReq(title, genre, poster, year, languages, actors);
                final ObjectMapper mapper = new ObjectMapper();
                final String reqDtoJson = mapper.writeValueAsString(reqDto);

                mvc.perform(post("/movies/").contentType(MediaType.APPLICATION_JSON).content(reqDtoJson))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
        }

        @Test
        void givenAValidId_whenDeleteMovie_thenReturnsVoid() throws Exception {
                final long id = 2L;

                mvc.perform(delete(new StringBuilder("/movies/").append(id).toString()).contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNoContent());
        }

        @Test
        void givenANoExistId_whenDeleteMovie_thenReturnsNotFound() throws Exception {
                final long id = Long.MAX_VALUE;

                mvc.perform(delete(new StringBuilder("/movies/").append(id).toString()).contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound());
        }

        @Test
        void givenANotValidId_whenDeleteMovie_thenReturnsBadRequest() throws Exception {
                final String id = "anyString";

                mvc.perform(delete(new StringBuilder("/movies/").append(id).toString()).contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
        }

        @Test
        void givenAValidMovie_whenUpdateMovie_thenReturnsAMovie() throws Exception {
                final String title = "superman IV";
                final String genre = "ACTION";
                final String poster = "http://www.freepic.com/anyPic.jpg";
                final int year = 1000;
                final String actor = "anyActor2";
                final List<String> languages = Collections.singletonList("Spanish");
                final List<ActorDto> actors = Arrays.asList(createActorDto(actor));
                final MovieReqDto reqDto = createMovieReq(title, genre, poster, year, languages, actors);
                final ObjectMapper mapper = new ObjectMapper();
                final String reqDtoJson = mapper.writeValueAsString(reqDto);

                mvc.perform(put("/movies/3").contentType(MediaType.APPLICATION_JSON).content(reqDtoJson))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.title", is(title)))
                        .andExpect(jsonPath("$.genre", is("Action")))
                        .andExpect(jsonPath("$.languages[0]", is("Spanish")))
                        .andExpect(jsonPath("$.year", is(year)))
                        .andExpect(jsonPath("$.actors[0].name", is(actor)));
        }

        @Test
        void givenANoValidMovie_whenUpdateMovie_thenReturnsBadRequest() throws Exception {
                final String title = "";
                final String genre = "ACTION";
                final String poster = "http://www.freepic.com/anyPic.jpg";
                final int year = 1000;
                final String actor = "anyActor2";
                final List<String> languages = Collections.singletonList("Spanish");
                final List<ActorDto> actors = Arrays.asList(createActorDto(actor));
                final MovieReqDto reqDto = createMovieReq(title, genre, poster, year, languages, actors);
                final ObjectMapper mapper = new ObjectMapper();
                final String reqDtoJson = mapper.writeValueAsString(reqDto);

                mvc.perform(put("/movies/2").contentType(MediaType.APPLICATION_JSON).content(reqDtoJson))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
        }

        @Test
        void givenANoValidId_whenUpdateMovie_thenReturnsNotFound() throws Exception {
                final Long notValidId = 20L;
                final String title = "anyTitle";
                final String genre = "ACTION";
                final String poster = "http://www.freepic.com/anyPic.jpg";
                final int year = 1000;
                final String actor = "anyActor2";
                final List<String> languages = Collections.singletonList("Spanish");
                final List<ActorDto> actors = Arrays.asList(createActorDto(actor));
                final MovieReqDto reqDto = createMovieReq(title, genre, poster, year, languages, actors);
                final ObjectMapper mapper = new ObjectMapper();
                final String reqDtoJson = mapper.writeValueAsString(reqDto);

                mvc.perform(put(new StringBuilder("/movies/").append(notValidId.toString()).toString()).contentType(MediaType.APPLICATION_JSON).content(reqDtoJson))
                        .andDo(print())
                        .andExpect(status().isNotFound());
        }

        @Test
        void givenANonPath_returnsMethodNotAllowed() throws Exception {
                mvc.perform(delete("/movies/").contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isMethodNotAllowed());
        }

        private void createMovieEntity(String title, String genre, String poster, int year, List<String> languages,
                                       List<ActorEntity> actors) {

                final MovieEntity entity = new MovieEntity();
                entity.setTitle(title);
                entity.setGenre(genre);
                entity.setPoster(poster);
                entity.setYear(year);
                entity.setLanguages(languages);
                entity.setActors(actors);
                moviesRepository.save(entity);
        }
        private ActorEntity createActorEntity(String name) {
                final ActorEntity entity = new ActorEntity();
                entity.setName(name);
                ActorEntity result = actorsRepository.save(entity);
                return result;
        }

        private MovieReqDto createMovieReq(String title, String genre, String poster, int year, List<String> languages,
                                       List<ActorDto> actors) {

                final MovieReqDto dto = new MovieReqDto();
                dto.setTitle(title);
                dto.setGenre(GenreDto.valueOf(genre));
                dto.setPoster(poster);
                dto.setYear(year);
                dto.setLanguages(languages.stream().map(s -> LanguageDto.fromValue(s)).collect(Collectors.toList()));
                dto.setActors(actors);

                return dto;
        }
        private ActorDto createActorDto(String name) {
                final ActorDto dto = new ActorDto();
                dto.setId(Long.MAX_VALUE);
                dto.setName(name);
                return dto;
        }

}
