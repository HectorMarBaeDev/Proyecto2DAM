package com.pokemon.pokemonbackend.repository;

import com.pokemon.pokemonbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // para el login por username
    Optional<User> findByUsername(String username);
}
