package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.model.Pokemon;
import com.pokemon.pokemonbackend.model.Team;
import com.pokemon.pokemonbackend.repository.PokemonRepository;
import com.pokemon.pokemonbackend.repository.TeamRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonRepository pokemonRepository;
    private final TeamRepository teamRepository;

    public PokemonController(PokemonRepository pokemonRepository, TeamRepository teamRepository) {
        this.pokemonRepository = pokemonRepository;
        this.teamRepository = teamRepository;
    }

    // Añadir Pokémon a un equipo
    @PostMapping
    public ResponseEntity<Pokemon> addPokemon(
            @Valid @RequestBody Pokemon pokemon,
            @RequestParam Long teamId
    ) {
        Team team = teamRepository.findById(teamId).orElse(null);

        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        // Regla de negocio: máximo 6 Pokémon
        if (pokemonRepository.findByTeam(team).size() >= 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        pokemon.setTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pokemonRepository.save(pokemon));
    }

    // Listar Pokémon de un equipo
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Pokemon>> getPokemonByTeam(@PathVariable Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);

        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pokemonRepository.findByTeam(team));
    }

    // Eliminar Pokémon
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        if (!pokemonRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        pokemonRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
