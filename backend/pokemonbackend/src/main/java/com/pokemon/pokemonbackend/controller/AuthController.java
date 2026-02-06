package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.AuthResponseDTO;
import com.pokemon.pokemonbackend.dto.LoginRequestDTO;
import com.pokemon.pokemonbackend.dto.RegisterRequestDTO;
import com.pokemon.pokemonbackend.model.User;
import com.pokemon.pokemonbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody RegisterRequestDTO dto
    ) {

        if (userRepository.existsByUsername(dto.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (userRepository.existsByEmail(dto.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));

        User saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(
                        saved.getId(),
                        saved.getUsername(),
                        saved.getEmail()
                ));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginRequestDTO dto
    ) {

        return userRepository.findByUsername(dto.username())
                .filter(user ->
                        passwordEncoder.matches(
                                dto.password(),
                                user.getPassword()
                        )
                )
                .map(user -> ResponseEntity.ok(
                        new AuthResponseDTO(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail()
                        )
                ))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
