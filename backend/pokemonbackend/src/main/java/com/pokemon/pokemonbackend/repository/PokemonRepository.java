package com.pokemon.pokemonbackend.repository;

import com.pokemon.pokemonbackend.model.Pokemon;
import com.pokemon.pokemonbackend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    List<Pokemon> findByTeam(Team team);
}
