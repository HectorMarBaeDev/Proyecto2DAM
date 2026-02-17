package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.PokemonListItemDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        Map<String, Object> data =
                pokeApiService.getPokemonData(request.getIdentifier());

        if (data == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer pokedexNumber = (Integer) data.get("id");
        String name = (String) data.get("name");
        String image = pokeApiService.getImage(data);

        Pokemon pokemon = new Pokemon(
                pokedexNumber,
                name,
                image,
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
                        saved.getImage(),
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
                        p.getImage(),
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

    @GetMapping("/index/page")
    public ResponseEntity<List<PokemonResponseDTO>> getIndexPokemonPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int pageSize
    ) {
        int totalPokemon = 1025;

        // Calcular offset aleatorio si quieres que cambie cada recarga
        Random random = new Random();
        int startId = random.nextInt(totalPokemon - pageSize + 1) + 1;

        List<PokemonResponseDTO> result = new ArrayList<>();

        for (int i = 0; i < pageSize; i++) {
            int pokemonId = startId + i;
            Map<String, Object> data = pokeApiService.getPokemonData(String.valueOf(pokemonId));
            if (data == null) continue;

            result.add(new PokemonResponseDTO(
                    (Integer) data.get("id"),
                    (Integer) data.get("id"),
                    (String) data.get("name"),
                    pokeApiService.getImage(data),
                    pokeApiService.getPrimaryType(data),
                    pokeApiService.getSecondaryType(data)
            ));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public List<PokemonListItemDTO> getAllPokemon() {
        return pokeApiService.getAllPokemonBasic();
    }

    // Obtener un Pokémon concreto por id
    @GetMapping("/{id}")
    public ResponseEntity<PokemonResponseDTO> getPokemonById(@PathVariable Long id) {

        Pokemon pokemon = pokemonRepository.findById(id).orElse(null);

        if (pokemon == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PokemonResponseDTO(
                pokemon.getId(),
                pokemon.getPokedexNumber(),
                pokemon.getName(),
                pokemon.getImage(),
                pokemon.getPrimaryType(),
                pokemon.getSecondaryType(),
                pokemon.getItem(),
                pokemon.getAbility(),
                pokemon.getMove1(),
                pokemon.getMove2(),
                pokemon.getMove3(),
                pokemon.getMove4(),
                pokemon.getHpIv(),
                pokemon.getAtkIv(),
                pokemon.getDefIv(),
                pokemon.getSpAtkIv(),
                pokemon.getSpDefIv(),
                pokemon.getSpeedIv(),
                pokemon.getHpEv(),
                pokemon.getAtkEv(),
                pokemon.getDefEv(),
                pokemon.getSpAtkEv(),
                pokemon.getSpDefEv(),
                pokemon.getSpeedEv()
        ));
    }

    // Actualizar build competitivo
    @PutMapping("/{id}")
    public ResponseEntity<PokemonResponseDTO> updatePokemon(
            @PathVariable Long id,
            @RequestBody PokemonResponseDTO dto
    ) {

        Pokemon pokemon = pokemonRepository.findById(id).orElse(null);

        if (pokemon == null) {
            return ResponseEntity.notFound().build();
        }

        // Solo actualizamos datos competitivos
        pokemon.setItem(dto.getItem());
        pokemon.setAbility(dto.getAbility());
        pokemon.setMove1(dto.getMove1());
        pokemon.setMove2(dto.getMove2());
        pokemon.setMove3(dto.getMove3());
        pokemon.setMove4(dto.getMove4());

        pokemon.setHpIv(dto.getHpIv());
        pokemon.setAtkIv(dto.getAtkIv());
        pokemon.setDefIv(dto.getDefIv());
        pokemon.setSpAtkIv(dto.getSpAtkIv());
        pokemon.setSpDefIv(dto.getSpDefIv());
        pokemon.setSpeedIv(dto.getSpeedIv());

        pokemon.setHpEv(dto.getHpEv());
        pokemon.setAtkEv(dto.getAtkEv());
        pokemon.setDefEv(dto.getDefEv());
        pokemon.setSpAtkEv(dto.getSpAtkEv());
        pokemon.setSpDefEv(dto.getSpDefEv());
        pokemon.setSpeedEv(dto.getSpeedEv());

        Pokemon saved = pokemonRepository.save(pokemon);

        return ResponseEntity.ok(new PokemonResponseDTO(
                saved.getId(),
                saved.getPokedexNumber(),
                saved.getName(),
                saved.getImage(),
                saved.getPrimaryType(),
                saved.getSecondaryType(),
                saved.getItem(),
                saved.getAbility(),
                saved.getMove1(),
                saved.getMove2(),
                saved.getMove3(),
                saved.getMove4(),
                saved.getHpIv(),
                saved.getAtkIv(),
                saved.getDefIv(),
                saved.getSpAtkIv(),
                saved.getSpDefIv(),
                saved.getSpeedIv(),
                saved.getHpEv(),
                saved.getAtkEv(),
                saved.getDefEv(),
                saved.getSpAtkEv(),
                saved.getSpDefEv(),
                saved.getSpeedEv()
        ));
    }




}