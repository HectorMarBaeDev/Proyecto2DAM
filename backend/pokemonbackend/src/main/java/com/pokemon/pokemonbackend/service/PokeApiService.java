package com.pokemon.pokemonbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PokeApiService {
    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getPokemonData (String identefier) {
        return restTemplate.getForObject(POKEAPI_URL + identefier , Map.class);
    }

    public String getPrimaryType (Map <String, Object> data) {
        List<Map<String, Object>> types = (List<Map<String, Object>>) data.get("types");
        return (String) ((Map<String, Object>)types.get(0).get("type")).get("name");
    }

    public String getSecondaryType (Map <String, Object> data) {
        List<Map<String, Object>> types = (List<Map<String, Object>>) data.get("types");
        if (types.size() < 1) {
            return (String) ((Map<String, Object>)types.get(0).get("type")).get("name");
        } else {
            return null;
        }
    }
}
