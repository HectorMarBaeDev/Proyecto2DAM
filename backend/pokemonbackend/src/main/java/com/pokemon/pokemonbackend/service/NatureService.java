package com.pokemon.pokemonbackend.service;

import java.util.HashMap;
import java.util.Map;

public class NatureService {

    public static Map<String, String> getNatureMap() {
        Map<String, String> map = new HashMap<>();

        map.put("adamant", "atk,spAtk");
        map.put("modest", "spAtk,atk");
        map.put("jolly", "speed,spAtk");
        map.put("timid", "speed,atk");
        map.put("bold", "def,atk");
        map.put("calm", "spDef,atk");
        map.put("careful", "spDef,spAtk");
        map.put("impish", "def,spAtk");

        return map;
    }
}
