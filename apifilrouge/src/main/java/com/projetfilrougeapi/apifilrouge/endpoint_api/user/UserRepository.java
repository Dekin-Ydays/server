package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
    //User findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
}
