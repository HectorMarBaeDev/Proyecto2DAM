package com.pokemon.pokemonbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pokemon")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private Integer pokedexNumber;

    @NotBlank
    private String name;

    @NotBlank
    private String image;

    @NotBlank
    private String primaryType;

    private String secondaryType;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public Pokemon() {}

    // ✅ CONSTRUCTOR CORRECTO
    public Pokemon(
            Integer pokedexNumber,
            String name,
            String image,
            String primaryType,
            String secondaryType,
            Team team
    ) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.image = image;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public Integer getPokedexNumber() {
        return pokedexNumber;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public Team getTeam() {
        return team;
    }

    public void setPokedexNumber(Integer pokedexNumber) {
        this.pokedexNumber = pokedexNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public void setSecondaryType(String secondaryType) {
        this.secondaryType = secondaryType;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
