package com.pokemon.pokemonbackend.dto;

public class PokemonResponseDTO {
    private Long id;
    private int pokedexNumber;
    private String name;
    private String primaryType;
    private String secondaryType;

    public PokemonResponseDTO(
            Long id,
            int pokedexNumber,
            String name,
            String primaryType,
            String secondaryType
    ) {
        this.id = id;
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
    }

    public Long getId() { return id; }
    public int getPokedexNumber() { return pokedexNumber; }
    public String getName() { return name; }
    public String getPrimaryType() { return primaryType; }
    public String getSecondaryType() { return secondaryType; }
}
