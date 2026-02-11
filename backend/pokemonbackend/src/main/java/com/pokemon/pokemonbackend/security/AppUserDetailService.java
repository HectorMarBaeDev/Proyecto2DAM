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
public class AppUserDetailService {
    private AppUserRepository appUserRepository;

    public AppUserDetailService(
            AppUserRepository appUserRepository
    ) {
        this.appUserRepository = appUserRepository;
    }

    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/user-details-service.html
    // https://medium.com/@davoud.badamchi/building-secure-spring-boot-applications-with-database-authentication-a-comprehensive-guide-6c8171979b5a
    @Bean
    UserDetailsService customUserDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                Optional<AppUser> optUser = appUserRepository.findByUsername(username);
                if (optUser.isPresent()) {
                    AppUser appUser = optUser.get();
                    return User.builder()
                            .username(appUser.getUsername())
                            .password(appUser.getPassword())
                            .authorities(appUser.getRole())
                            .build();

                } else {
                    throw new UsernameNotFoundException(username);
                }
            }
        };
    }
}
