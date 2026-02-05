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

    // 🔐 USUARIO AUTENTICADO
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

    // 🛠 DEBUG / ADMIN
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        return ResponseEntity.ok(
                userRepository.findAll()
                        .stream()
                        .map(u -> new UserResponseDTO(
                                u.getId(),
                                u.getUsername(),
                                u.getEmail()
                        ))
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
