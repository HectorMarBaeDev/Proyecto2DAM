package com.pokemon.pokemonbackend.dto;

public class PokemonResponseDTO {

    private int id;
    private int pokedexNumber;
    private String name;
    private String image;
    private String primaryType;
    private String secondaryType;

    private String item;
    private String ability;

    private String move1;
    private String move2;
    private String move3;
    private String move4;

    private Integer hpIv;
    private Integer atkIv;
    private Integer defIv;
    private Integer spAtkIv;
    private Integer spDefIv;
    private Integer speedIv;

    private Integer hpEv;
    private Integer atkEv;
    private Integer defEv;
    private Integer spAtkEv;
    private Integer spDefEv;
    private Integer speedEv;

    public PokemonResponseDTO(
            int id,
            int pokedexNumber,
            String name,
            String image,
            String primaryType,
            String secondaryType,
            String item,
            String ability,
            String move1,
            String move2,
            String move3,
            String move4,
            Integer hpIv,
            Integer atkIv,
            Integer defIv,
            Integer spAtkIv,
            Integer spDefIv,
            Integer speedIv,
            Integer hpEv,
            Integer atkEv,
            Integer defEv,
            Integer spAtkEv,
            Integer spDefEv,
            Integer speedEv
    ) {
        this.id = id;
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.image = image;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.item = item;
        this.ability = ability;
        this.move1 = move1;
        this.move2 = move2;
        this.move3 = move3;
        this.move4 = move4;
        this.hpIv = hpIv;
        this.atkIv = atkIv;
        this.defIv = defIv;
        this.spAtkIv = spAtkIv;
        this.spDefIv = spDefIv;
        this.speedIv = speedIv;
        this.hpEv = hpEv;
        this.atkEv = atkEv;
        this.defEv = defEv;
        this.spAtkEv = spAtkEv;
        this.spDefEv = spDefEv;
        this.speedEv = speedEv;
    }

    // Constructor básico (para listados)
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

    public String getItem() { return item; }
    public String getAbility() { return ability; }

    public String getMove1() { return move1; }
    public String getMove2() { return move2; }
    public String getMove3() { return move3; }
    public String getMove4() { return move4; }

    public Integer getHpIv() { return hpIv; }
    public Integer getAtkIv() { return atkIv; }
    public Integer getDefIv() { return defIv; }
    public Integer getSpAtkIv() { return spAtkIv; }
    public Integer getSpDefIv() { return spDefIv; }
    public Integer getSpeedIv() { return speedIv; }

    public Integer getHpEv() { return hpEv; }
    public Integer getAtkEv() { return atkEv; }
    public Integer getDefEv() { return defEv; }
    public Integer getSpAtkEv() { return spAtkEv; }
    public Integer getSpDefEv() { return spDefEv; }
    public Integer getSpeedEv() { return speedEv; }
}
