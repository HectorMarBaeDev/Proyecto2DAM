package com.pokemon.pokemonbackend.config;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.pokemon.pokemonbackend.storage.StorageProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author franp
 */
@Configuration
public class FileLoadConfiguration {
    @Bean
    public StorageProperties getStorageProperties() {
        return new StorageProperties();
    }   
}
