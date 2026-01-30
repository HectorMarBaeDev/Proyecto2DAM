package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.PokemonRequestDTO;
import com.pokemon.pokemonbackend.dto.PokemonResponseDTO;
import com.pokemon.pokemonbackend.model.Pokemon;
import com.pokemon.pokemonbackend.model.Team;
import com.pokemon.pokemonbackend.repository.PokemonRepository;
import com.pokemon.pokemonbackend.repository.TeamRepository;
import com.pokemon.pokemonbackend.service.PokeApiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonRepository pokemonRepository;
    private final TeamRepository teamRepository;
    private final PokeApiService pokeApiService;

    public PokemonController(
            PokemonRepository pokemonRepository,
            TeamRepository teamRepository,
            PokeApiService pokeApiService
    ) {
        this.pokemonRepository = pokemonRepository;
        this.teamRepository = teamRepository;
        this.pokeApiService = pokeApiService;
    }

    // Añadir Pokémon a un equipo
    @PostMapping
    public ResponseEntity<PokemonResponseDTO> addPokemon(
            @Valid @RequestBody PokemonRequestDTO request,
            @RequestParam Long teamId
    ) {
        Team team = teamRepository.findById(teamId).orElse(null);

        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        // Máximo 6 Pokémon
        if (pokemonRepository.findByTeam(team).size() >= 6) {
            return ResponseEntity.badRequest().build();
        }

        // 🔴 AQUÍ VA LA COMPROBACIÓN
        Map<String, Object> data =
                pokeApiService.getPokemonData(request.getIdentifier());

        if (data == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer pokedexNumber = (Integer) data.get("id");
        String name = (String) data.get("name");

        Pokemon pokemon = new Pokemon(
                pokedexNumber,
                name,
                pokeApiService.getPrimaryType(data),
                pokeApiService.getSecondaryType(data),
                team
        );

        Pokemon saved = pokemonRepository.save(pokemon);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PokemonResponseDTO(
                        saved.getId(),
                        saved.getPokedexNumber(),
                        saved.getName(),
                        saved.getPrimaryType(),
                        saved.getSecondaryType()
                ));
    }


    // Listar Pokémon de un equipo
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PokemonResponseDTO>> getPokemonByTeam(@PathVariable Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);

        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        List<PokemonResponseDTO> response = pokemonRepository.findByTeam(team)
                .stream()
                .map(p -> new PokemonResponseDTO(
                        p.getId(),
                        p.getPokedexNumber(),
                        p.getName(),
                        p.getPrimaryType(),
                        p.getSecondaryType()
                ))
                .toList();

        return ResponseEntity.ok(response);
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