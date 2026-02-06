package com.pokemon.pokemonbackend.dto;

public record RegisterRequestDTO(
        String username,
        String email,
        String password
) {}
