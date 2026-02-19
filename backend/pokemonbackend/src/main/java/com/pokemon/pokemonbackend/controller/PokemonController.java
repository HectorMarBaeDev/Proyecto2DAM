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

import java.util.*;

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

    // ---------------- ADD ----------------

    @PostMapping
    public ResponseEntity<PokemonResponseDTO> addPokemon(
            @Valid @RequestBody PokemonRequestDTO request,
            @RequestParam Long teamId
    ) {
        Team team = teamRepository.findById(teamId).orElse(null);

        if (team == null) return ResponseEntity.notFound().build();
        if (pokemonRepository.findByTeam(team).size() >= 6)
            return ResponseEntity.badRequest().build();

        Map<String, Object> data =
                pokeApiService.getPokemonData(request.getIdentifier());

        if (data == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Pokemon pokemon = new Pokemon(
                (Integer) data.get("id"),
                (String) data.get("name"),
                pokeApiService.getImage(data),
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

    // ---------------- LIST TEAM ----------------

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PokemonResponseDTO>> getPokemonByTeam(@PathVariable Long teamId) {

        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) return ResponseEntity.notFound().build();

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

    // ---------------- DELETE ----------------

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        if (!pokemonRepository.existsById(id))
            return ResponseEntity.notFound().build();

        pokemonRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- INDEX RANDOM ----------------

    @GetMapping("/index/page")
    public ResponseEntity<List<PokemonResponseDTO>> getIndexPokemonPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int pageSize
    ) {

        int totalPokemon = 1025;
        Random random = new Random();
        int startId = random.nextInt(totalPokemon - pageSize + 1) + 1;

        List<PokemonResponseDTO> result = new ArrayList<>();

        for (int i = 0; i < pageSize; i++) {

            Map<String, Object> data =
                    pokeApiService.getPokemonData(String.valueOf(startId + i));

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

    // ---------------- GET ALL ----------------

    @GetMapping
    public List<PokemonListItemDTO> getAllPokemon() {
        return pokeApiService.getAllPokemonBasic();
    }

    // ---------------- GET BY ID ----------------

    @GetMapping("/id/{id}")
    public ResponseEntity<PokemonResponseDTO> getPokemonById(@PathVariable Long id) {

        Pokemon pokemon = pokemonRepository.findById(id).orElse(null);
        if (pokemon == null) return ResponseEntity.notFound().build();

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

    // ---------------- UPDATE ----------------

    @PutMapping("/id/{id}")
    public ResponseEntity<PokemonResponseDTO> updatePokemon(
            @PathVariable Long id,
            @RequestBody PokemonResponseDTO dto
    ) {

        Pokemon pokemon = pokemonRepository.findById(id).orElse(null);
        if (pokemon == null) return ResponseEntity.notFound().build();

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

    // ---------------- ABILITIES ----------------

    @GetMapping("/id/{id}/abilities")
    public ResponseEntity<List<String>> getPokemonAbilities(@PathVariable Long id) {

        Pokemon pokemon = pokemonRepository.findById(id).orElse(null);
        if (pokemon == null) return ResponseEntity.notFound().build();

        Map<String, Object> data =
                pokeApiService.getPokemonData(pokemon.getName());

        if (data == null) return ResponseEntity.badRequest().build();

        List<Map<String, Object>> abilities =
                (List<Map<String, Object>>) data.get("abilities");

        List<String> abilityNames = new ArrayList<>();

        for (Map<String, Object> abilityEntry : abilities) {
            Map<String, Object> ability =
                    (Map<String, Object>) abilityEntry.get("ability");
            abilityNames.add((String) ability.get("name"));
        }

        return ResponseEntity.ok(abilityNames);
    }

    // ---------------- ITEMS PAGINATED ----------------

    @GetMapping("/competitive-items")
    public ResponseEntity<List<String>> getCompetitiveItems() {

        List<String> items = List.of(
                "leftovers",
                "choice-band",
                "choice-scarf",
                "choice-specs",
                "life-orb",
                "focus-sash",
                "assault-vest",
                "rocky-helmet",
                "air-balloon",
                "heavy-duty-boots",
                "sitrus-berry",
                "lum-berry",
                "weakness-policy",
                "eviolite",
                "expert-belt",
                "black-sludge",
                "light-clay"
        );

        return ResponseEntity.ok(items);
    }

    // MOVES

    @GetMapping("/{id}/moves")
    public ResponseEntity<List<String>> getPokemonMoves(@PathVariable Long id) {

        Pokemon pokemon = pokemonRepository.findById(id).orElse(null);

        if (pokemon == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> data =
                pokeApiService.getPokemonData(pokemon.getName());

        if (data == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Map<String, Object>> moves =
                (List<Map<String, Object>>) data.get("moves");

        Set<String> moveNames = new HashSet<>();

        for (Map<String, Object> moveEntry : moves) {
            Map<String, Object> move =
                    (Map<String, Object>) moveEntry.get("move");

            moveNames.add((String) move.get("name"));
        }

        List<String> result = new ArrayList<>(moveNames);
        Collections.sort(result);

        return ResponseEntity.ok(result);
    }



}
