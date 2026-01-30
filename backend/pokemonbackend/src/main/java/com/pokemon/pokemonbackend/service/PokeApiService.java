package com.pokemon.pokemonbackend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class PokeApiService {

    private static final String POKEAPI_URL =
            "https://pokeapi.co/api/v2/pokemon/";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getPokemonData(String identifier) {

        try {
            Map<String, Object> data = restTemplate.getForObject(
                    POKEAPI_URL + identifier.toLowerCase(),
                    Map.class
            );

            if (data == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pokémon no encontrado"
                );
            }

            return data;

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Error consultando la PokeAPI"
            );
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

        if (data == null || !data.containsKey("types")) {
            return null;
        }

        Object typesObj = data.get("types");
        if (!(typesObj instanceof List<?> types)) {
            return null;
        }

        for (Object entryObj : types) {
            if (!(entryObj instanceof Map<?, ?> entry)) {
                continue;
            }

            Object slotObj = entry.get("slot");
            if (!(slotObj instanceof Number slotNumber)) {
                continue;
            }

            if (slotNumber.intValue() == slot) {
                Object typeObj = entry.get("type");
                if (typeObj instanceof Map<?, ?> typeMap) {
                    Object name = typeMap.get("name");
                    return name != null ? name.toString() : null;
                }
            }
        }

        return null;
    }
}
