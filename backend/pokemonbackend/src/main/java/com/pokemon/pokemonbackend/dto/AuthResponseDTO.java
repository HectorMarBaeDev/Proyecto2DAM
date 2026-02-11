package com.pokemon.pokemonbackend.dto;

public class AuthResponseDTO {

    private String token;
    private Long userId;
    private String username;

    public AuthResponseDTO(String token, Long userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
