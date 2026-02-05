package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.UserResponseDTO;
import com.pokemon.pokemonbackend.model.User;
import com.pokemon.pokemonbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔐 SIEMPRE ARRIBA
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                )
        );
    }

    // 🔢 DESPUÉS
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

        return userRepository.findById(id)
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

