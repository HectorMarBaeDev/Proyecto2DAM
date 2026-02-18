package com.pokemon.pokemonbackend.service;

import java.util.Map;

public class PokemonStatService {

    public static int calculateHp(int base, int iv, int ev, int level) {
        return (int) Math.floor(
                ((2 * base + iv + (ev / 4.0)) * level) / 100.0
        ) + level + 10;
    }

    public static int calculateOtherStat(
            int base,
            int iv,
            int ev,
            int level,
            double natureMultiplier
    ) {
        double value = Math.floor(
                ((2 * base + iv + (ev / 4.0)) * level) / 100.0
        ) + 5;

        return (int) Math.floor(value * natureMultiplier);
    }

    public static double getNatureMultiplier(
            String nature,
            String stat,
            Map<String, String> natureMap
    ) {
        if (nature == null || !natureMap.containsKey(nature)) {
            return 1.0;
        }

        String boosted = natureMap.get(nature).split(",")[0];
        String lowered = natureMap.get(nature).split(",")[1];

        if (stat.equals(boosted)) return 1.1;
        if (stat.equals(lowered)) return 0.9;

        return 1.0;
    }
}
