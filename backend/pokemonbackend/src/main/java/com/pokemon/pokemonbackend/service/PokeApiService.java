package com.pokemon.pokemonbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PokeApiService {

    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/";
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getPokemonData(String identifier) {

        String normalized = identifier.trim().toLowerCase();

        try {
            return restTemplate.getForObject(
                    POKEAPI_URL + normalized,
                    Map.class
            );
        } catch (HttpClientErrorException e) {
            return null; // Pokémon no existe
        }
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
}
