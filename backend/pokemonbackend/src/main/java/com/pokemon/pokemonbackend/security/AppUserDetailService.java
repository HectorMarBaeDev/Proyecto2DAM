package com.pokemon.pokemonbackend.security;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Optional;

import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author franp
 */
@Service
public class AppUserDetailService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(appUser.getRole())
                .build();
    }
}
