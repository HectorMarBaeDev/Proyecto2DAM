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

    private String item;

    private String ability;

    private String move1;
    private String move2;
    private String move3;
    private String move4;

    private Integer hpIv = 31;
    private Integer atkIv = 31;
    private Integer defIv = 31;
    private Integer spAtkIv = 31;
    private Integer spDefIv = 31;
    private Integer speedIv = 31;

    private Integer hpEv = 0;
    private Integer atkEv = 0;
    private Integer defEv = 0;
    private Integer spAtkEv = 0;
    private Integer spDefEv = 0;
    private Integer speedEv = 0;


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

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getMove1() {
        return move1;
    }

    public void setMove1(String move1) {
        this.move1 = move1;
    }

    public String getMove2() {
        return move2;
    }

    public void setMove2(String move2) {
        this.move2 = move2;
    }

    public String getMove3() {
        return move3;
    }

    public void setMove3(String move3) {
        this.move3 = move3;
    }

    public String getMove4() {
        return move4;
    }

    public void setMove4(String move4) {
        this.move4 = move4;
    }

    public Integer getHpIv() {
        return hpIv;
    }

    public void setHpIv(Integer hpIv) {
        this.hpIv = hpIv;
    }

    public Integer getAtkIv() {
        return atkIv;
    }

    public void setAtkIv(Integer atkIv) {
        this.atkIv = atkIv;
    }

    public Integer getDefIv() {
        return defIv;
    }

    public void setDefIv(Integer defIv) {
        this.defIv = defIv;
    }

    public Integer getSpAtkIv() {
        return spAtkIv;
    }

    public void setSpAtkIv(Integer spAtkIv) {
        this.spAtkIv = spAtkIv;
    }

    public Integer getSpDefIv() {
        return spDefIv;
    }

    public void setSpDefIv(Integer spDefIv) {
        this.spDefIv = spDefIv;
    }

    public Integer getSpeedIv() {
        return speedIv;
    }

    public void setSpeedIv(Integer speedIv) {
        this.speedIv = speedIv;
    }

    public Integer getAtkEv() {
        return atkEv;
    }

    public void setAtkEv(Integer atkEv) {
        this.atkEv = atkEv;
    }

    public Integer getHpEv() {
        return hpEv;
    }

    public void setHpEv(Integer hpEv) {
        this.hpEv = hpEv;
    }

    public Integer getDefEv() {
        return defEv;
    }

    public void setDefEv(Integer defEv) {
        this.defEv = defEv;
    }

    public Integer getSpAtkEv() {
        return spAtkEv;
    }

    public void setSpAtkEv(Integer spAtkEv) {
        this.spAtkEv = spAtkEv;
    }

    public Integer getSpDefEv() {
        return spDefEv;
    }

    public void setSpDefEv(Integer spDefEv) {
        this.spDefEv = spDefEv;
    }

    public Integer getSpeedEv() {
        return speedEv;
    }

    public void setSpeedEv(Integer speedEv) {
        this.speedEv = speedEv;
    }
}
