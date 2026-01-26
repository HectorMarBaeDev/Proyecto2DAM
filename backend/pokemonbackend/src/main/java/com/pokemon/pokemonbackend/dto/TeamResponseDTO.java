package com.pokemon.pokemonbackend.dto;

public class TeamResponseDTO {
    private Long id;
    private String name;
    private String format;
    private Long userId;

    public TeamResponseDTO(Long id, String name, String format, Long userId) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public Long getUserId() {
        return userId;
    }
}
