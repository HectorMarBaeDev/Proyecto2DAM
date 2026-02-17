package com.pokemon.pokemonbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.pokemon.pokemonbackend.dto.PokemonListItemDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PokeApiService {

    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/";
    private final RestTemplate restTemplate = new RestTemplate();

    // 🔥 Cache en memoria
    private List<PokemonListItemDTO> cachedPokemonList;

    // 🔥 Precarga automática al iniciar el servidor
    @jakarta.annotation.PostConstruct
    public void preloadPokemon() {
        getAllPokemonBasic();
    }

    public Map<String, Object> getPokemonData(String identifier) {

        String normalized = identifier.trim().toLowerCase();

        try {
            return restTemplate.getForObject(
                    POKEAPI_URL + normalized,
                    Map.class
            );
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String getImage(Map<String, Object> data) {
        if (data == null) return null;

        Map<String, Object> sprites = (Map<String, Object>) data.get("sprites");
        if (sprites == null) return null;

        return (String) sprites.get("front_default");
    }

    public String getPrimaryType(Map<String, Object> data) {
        return getTypeBySlot(data, 1);
    }

    public String getSecondaryType(Map<String, Object> data) {
        return getTypeBySlot(data, 2);
    }

    @SuppressWarnings("unchecked")
    private String getTypeBySlot(Map<String, Object> data, int slot) {
        List<Map<String, Object>> types =
                (List<Map<String, Object>>) data.get("types");

        for (Map<String, Object> typeEntry : types) {
            Integer typeSlot = (Integer) typeEntry.get("slot");
            if (typeSlot != null && typeSlot == slot) {
                Map<String, Object> type =
                        (Map<String, Object>) typeEntry.get("type");
                return (String) type.get("name");
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<PokemonListItemDTO> getAllPokemonBasic() {

        if (cachedPokemonList != null) {
            return cachedPokemonList;
        }

        String baseUrl = "https://pokeapi.co/api/v2/pokemon?limit=1";

        Map<String, Object> countResponse =
                restTemplate.getForObject(baseUrl, Map.class);

        Integer total = (Integer) countResponse.get("count");

        String url = "https://pokeapi.co/api/v2/pokemon?limit=" + total;

        Map<String, Object> response =
                restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.get("results");

        List<PokemonListItemDTO> pokemonList = new ArrayList<>();

        int pokedexNumber = 1;

        for (Map<String, Object> pokemon : results) {

            String name = (String) pokemon.get("name");

            // 🔥 Llamada individual para obtener tipos
            Map<String, Object> fullData =
                    restTemplate.getForObject(
                            POKEAPI_URL + pokedexNumber,
                            Map.class
                    );

            String primaryType = getPrimaryType(fullData);
            String secondaryType = getSecondaryType(fullData);

            pokemonList.add(
                    new PokemonListItemDTO(
                            pokedexNumber,
                            name,
                            primaryType,
                            secondaryType
                    )
            );

            pokedexNumber++;
        }

        cachedPokemonList = pokemonList;

        return cachedPokemonList;
    }



        // 🔥 Guardar en memoria
        cachedPokemonList = pokemonList;

        return cachedPokemonList;
    }
}
