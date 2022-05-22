package com.trilobyte.pelisdb;

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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        void givenAValidId_whenGetById_thenReturnsASuperhero() throws Exception {
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

}
