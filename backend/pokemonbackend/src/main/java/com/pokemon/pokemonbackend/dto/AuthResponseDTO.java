package com.pokemon.pokemonbackend.dto;

public record AuthResponseDTO(
        Long id,
        String username,
        String email
) {}

