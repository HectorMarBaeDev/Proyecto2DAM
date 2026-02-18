package com.pokemon.pokemonbackend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.pokemon.pokemonbackend.dto.PokemonListItemDTO;

import java.util.ArrayList;
import java.util.HashMap;
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
    @SuppressWarnings("unchecked")
    public Map<String, Integer> extractBaseStats(Map<String, Object> data) {

        List<Map<String, Object>> stats =
                (List<Map<String, Object>>) data.get("stats");

        Map<String, Integer> result = new HashMap<>();

        for (Map<String, Object> statEntry : stats) {

            int baseStat = (Integer) statEntry.get("base_stat");

            Map<String, Object> statInfo =
                    (Map<String, Object>) statEntry.get("stat");

            String statName = (String) statInfo.get("name");

            switch (statName) {
                case "hp" -> result.put("hp", baseStat);
                case "attack" -> result.put("atk", baseStat);
                case "defense" -> result.put("def", baseStat);
                case "special-attack" -> result.put("spAtk", baseStat);
                case "special-defense" -> result.put("spDef", baseStat);
                case "speed" -> result.put("speed", baseStat);
            }
        }

        return result;
    }

    public Map<String, String> getNatureMap() {

        Map<String, String> natureMap = new HashMap<>();

        natureMap.put("adamant", "atk,spAtk");
        natureMap.put("modest", "spAtk,atk");
        natureMap.put("jolly", "speed,spAtk");
        natureMap.put("timid", "speed,atk");
        natureMap.put("bold", "def,atk");
        natureMap.put("calm", "spDef,atk");
        natureMap.put("careful", "spDef,spAtk");
        natureMap.put("impish", "def,spAtk");
        natureMap.put("naive", "speed,spDef");
        natureMap.put("hasty", "speed,def");
        natureMap.put("brave", "atk,speed");
        natureMap.put("quiet", "spAtk,speed");
        natureMap.put("relaxed", "def,speed");
        natureMap.put("sassy", "spDef,speed");
        natureMap.put("rash", "spAtk,spDef");
        natureMap.put("mild", "spAtk,def");
        natureMap.put("gentle", "spDef,def");
        natureMap.put("lonely", "atk,def");
        natureMap.put("naughty", "atk,spDef");

        return natureMap;
    }




}

