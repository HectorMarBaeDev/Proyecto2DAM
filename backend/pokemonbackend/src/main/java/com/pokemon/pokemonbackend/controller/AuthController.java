package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.RegisterRequest;
import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // -------- REGISTER --------
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {

        if (repo.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        AppUser appUser = new AppUser(
                req.getUsername(),
                encoder.encode(req.getPassword()),
                "ROLE_USER",
                req.getEmail()
        );

        repo.save(appUser);
        return ResponseEntity.ok("Usuario registrado");
    }

    // -------- LOGIN CHECK --------
    @PostMapping("/login")
    public ResponseEntity<?> login() {
        // Si llega aquí, Basic Auth ya validó
        return ResponseEntity.ok("Login OK");
    }
}

