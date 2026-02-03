package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.UserResponseDTO;
import com.pokemon.pokemonbackend.model.User;
import com.pokemon.pokemonbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {
        User saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponseDTO(
                        saved.getId(),
                        saved.getUsername(),
                        saved.getEmail()
                ));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> response = userRepository.findAll()
                .stream()
                .map(u -> new UserResponseDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    // READ BY ID
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

    // READ BY USERNAME (LOGIN SIMPLE)
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {

        return userRepository.findByUsername(username)
                .map(u -> ResponseEntity.ok(
                        new UserResponseDTO(
                                u.getId(),
                                u.getUsername(),
                                u.getEmail()
                        )
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
