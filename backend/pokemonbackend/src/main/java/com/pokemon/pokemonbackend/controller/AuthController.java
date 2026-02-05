package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.LoginRequest;
import com.pokemon.pokemonbackend.dto.RegisterRequest;
import com.pokemon.pokemonbackend.model.User;
import com.pokemon.pokemonbackend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // -------- REGISTER --------
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {

        if (repo.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        User user = new User(
                req.getUsername(),
                encoder.encode(req.getPassword()),
                "ROLE_USER",
                req.getEmail()
        );

        repo.save(user);
        return ResponseEntity.ok("Usuario registrado");
    }

    // -------- LOGIN CHECK --------
    @PostMapping("/login")
    public ResponseEntity<?> login() {
        // Si llega aquí, Basic Auth ya validó
        return ResponseEntity.ok("Login OK");
    }
}

