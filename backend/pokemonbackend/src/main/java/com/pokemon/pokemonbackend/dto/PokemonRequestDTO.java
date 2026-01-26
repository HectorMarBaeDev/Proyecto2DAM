package com.pokemon.pokemonbackend.dto;

import jakarta.validation.constraints.NotBlank;

public class PokemonRequestDTO {

    @NotBlank
    private String identifier; // nombre o número

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}