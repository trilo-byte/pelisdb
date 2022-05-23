package com.trilobyte.pelisdb.mappers;

import com.trilobyte.pelisdb.dto.ActorDto;
import com.trilobyte.pelisdb.entities.ActorEntity;
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
@ContextConfiguration(classes = {ActorMapperTest.class})
public class ActorMapperTest {

    @InjectMocks private ActorMapper mapper = new ActorMapperImpl();

    @Test
    void toEntity_returnsEntity_FromValidActorDto() {
        // GIVEN
        final Long id = 1L;
        final String name = "anyName";
        final ActorDto dto = new ActorDto();
        dto.setId(id);
        dto.setName(name);

        // WHEN
        final ActorEntity response = mapper.toEntity(dto);

        // THEN
        assertThat(response).isNotNull();
        assertEquals(name, response.getName());
        assertEquals(id, response.getId());
    }

    @Test
    void toEntity_returnsNull_fromNullActorDto() {
        // GIVEN

        // WHEN
        final ActorEntity response = mapper.toEntity(null);

        // THEN
        assertThat(response).isNull();
    }

    @Test
    void toDto_RetunsActorDto_fromValidEntity() {
        // GIVEN
        final Long id = 1L;
        final String name = "anyName";
        final ActorEntity entity = new ActorEntity();
        entity.setId(id);
        entity.setName(name);

        // WHEN
        final ActorDto response = mapper.toDto(entity);

        // THEN
        assertThat(response).isNotNull();
        assertEquals(name, response.getName());
        assertEquals(id, response.getId());
    }

    @Test
    void toDto_ReturnsActorDto_fromNotValidEntity() {
        // GIVEN
        final ActorEntity entity = new ActorEntity();

        // WHEN
        final ActorDto response = mapper.toDto(entity);

        //THEN
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
    }

    @Test
    void toDto_returnActorDtoList_fromValidEntityList() {
        // GIVEN
        final Long id1 = 1L;
        final String name1 = "anyName1";
        final ActorEntity entity1 = new ActorEntity();
        entity1.setId(id1);
        entity1.setName(name1);
        final Long id2 = 2L;
        final String name2 = "anyName2";
        final ActorEntity entity2 = new ActorEntity();
        entity2.setId(id2);
        entity2.setName(name2);
        List<ActorEntity> list = Arrays.asList(entity1, entity2);

        // WHEN
        final List<ActorDto> response = mapper.toDtoList(list);

        // THEN
        assertThat(response).isNotNull();
        assertEquals(2, response.size());
        assertEquals(id1, response.get(0).getId());
        assertEquals(name1, response.get(0).getName());
        assertEquals(id2, response.get(1).getId());
        assertEquals(name2, response.get(1).getName());
    }
}
