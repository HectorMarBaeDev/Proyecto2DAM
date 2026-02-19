
package com.pokemon.pokemonbackend.dto;

public class MoveResponseDTO {

    private String name;
    private String type;
    private String category;
    private Integer power;

    public MoveResponseDTO(String name, String type, String category, Integer power) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.power = power;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public Integer getPower() { return power; }
}
