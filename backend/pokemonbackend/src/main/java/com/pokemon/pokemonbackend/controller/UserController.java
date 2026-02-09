package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.UserResponseDTO;
import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AppUserRepository appUserRepository;

    public UserController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // 🔐 USUARIO AUTENTICADO
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(Authentication authentication) {

        String username = authentication.getName();

        AppUser appUser = appUserRepository
                .findByUsername(username)
                .orElseThrow();

        return ResponseEntity.ok(
                new UserResponseDTO(
                        appUser.getId(),
                        appUser.getUsername(),
                        appUser.getEmail()
                )
        );
    }


    // 🔢 SOLO IDs NUMÉRICOS
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

        return appUserRepository.findById(id)
                .map(u -> ResponseEntity.ok(
                        new UserResponseDTO(
                                u.getId(),
                                u.getUsername(),
                                u.getEmail()
                        )
                ))
                .orElse(ResponseEntity.notFound().build());
    }

}

