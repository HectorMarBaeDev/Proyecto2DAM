package com.pokemon.pokemonbackend.service;

import jakarta.annotation.PostConstruct;
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

    // Cache en memoria
    private List<PokemonListItemDTO> cachedPokemonList;

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

        if (types == null) return null;

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

        for (Map<String, Object> pokemon : results) {

            String name = (String) pokemon.get("name");
            String pokemonUrl = (String) pokemon.get("url");

            Map<String, Object> fullData;

            try {
                fullData = restTemplate.getForObject(
                        pokemonUrl,
                        Map.class
                );
            } catch (Exception e) {
                continue; // si uno falla, seguimos
            }

            String primaryType = getPrimaryType(fullData);
            String secondaryType = getSecondaryType(fullData);

            // Extraer número real desde la URL
            String[] parts = pokemonUrl.split("/");
            int pokedexNumber = Integer.parseInt(
                    parts[parts.length - 1].isEmpty()
                            ? parts[parts.length - 2]
                            : parts[parts.length - 1]
            );

            pokemonList.add(
                    new PokemonListItemDTO(
                            pokedexNumber,
                            name,
                            primaryType,
                            secondaryType
                    )
            );
        }

        cachedPokemonList = pokemonList;

        return cachedPokemonList;
    }



    public Map<String, Object> getItemsPage(int offset, int limit) {

        String url = "https://pokeapi.co/api/v2/item?offset="
                + offset + "&limit=" + limit;

        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isCompetitiveCategory(String category) {

        return List.of(
                "held-items",
                "choice-items",
                "type-enhancement",
                "plates",
                "mega-stones",
                "memories",
                "z-crystals"
        ).contains(category);
    }

}

