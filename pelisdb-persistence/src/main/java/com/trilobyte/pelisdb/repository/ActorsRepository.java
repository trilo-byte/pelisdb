package com.trilobyte.pelisdb.repository;

import com.trilobyte.pelisdb.entities.ActorEntity;
import com.trilobyte.pelisdb.entities.ActorEntity_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

public interface ActorsRepository extends JpaRepository<ActorEntity, Long>, JpaSpecificationExecutor<ActorEntity> {

    public static Specification<ActorEntity> nameContains(final String name) {
        if (StringUtils.hasText(name)) {
            return (hero, cq, cb) ->
                    cb.like(cb.lower(hero.get(ActorEntity_.NAME)), new StringBuilder("%").append(name.toLowerCase()).append("%").toString());
        }
        return null;
    }
}
