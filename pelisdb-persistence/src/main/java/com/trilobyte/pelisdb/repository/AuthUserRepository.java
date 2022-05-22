package com.trilobyte.pelisdb.repository;

import com.trilobyte.pelisdb.entities.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long> {

    Optional<AuthUserEntity> findOneByEmail(String email);
}
