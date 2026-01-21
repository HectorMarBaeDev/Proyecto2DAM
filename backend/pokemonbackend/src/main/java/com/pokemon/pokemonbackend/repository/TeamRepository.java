package com.pokemon.pokemonbackend.repository;

import com.pokemon.pokemonbackend.model.Team;
import com.pokemon.pokemonbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    List<Team> findByUser(User user);
}
