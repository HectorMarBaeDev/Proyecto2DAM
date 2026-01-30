package com.pokemon.pokemonbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="pokemon")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private Integer pokedexNumber;

    @NotBlank
    private String name;

    @NotBlank
    private String primaryType;

    private String secondaryType;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public Pokemon() {}
    public Pokemon(int id, String name, String primaryType, String secondaryType, Team team) {
        this.id = id;
        this.name = name;
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

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public Team getTeam() {
        return team;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPokedexNumber(Integer pokedexNumber) {
        this.pokedexNumber = pokedexNumber;
    }

    public void setName(String name) {
        this.name = name;
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
