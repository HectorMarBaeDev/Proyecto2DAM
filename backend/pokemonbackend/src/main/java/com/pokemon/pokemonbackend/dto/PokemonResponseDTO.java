package com.pokemon.pokemonbackend.dto;

public class PokemonResponseDTO {
    private int id;
    private int pokedexNumber;
    private String name;
    private String image;
    private String primaryType;
    private String secondaryType;

    public PokemonResponseDTO(
            int id,
            int pokedexNumber,
            String name,
            String image,
            String primaryType,
            String secondaryType
    ) {
        this.id = id;
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.image = image;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
    }

    public int getId() { return id; }
    public int getPokedexNumber() { return pokedexNumber; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getPrimaryType() { return primaryType; }
    public String getSecondaryType() { return secondaryType; }
}
