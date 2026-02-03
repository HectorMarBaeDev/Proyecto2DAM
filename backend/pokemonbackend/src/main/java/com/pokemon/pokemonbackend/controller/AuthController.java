package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.RegisterRequest;
import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.Authenticator;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (repo.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        AppUser user = new AppUser(
                req.getUsername(),
                encoder.encode(req.getPassword()),
                "ROLE_USER"
        );

        repo.save(user);
        return ResponseEntity.ok("Usuario registrado");
    }

    @PostMapping("/login-check")
    public ResponseEntity<?> checkLogin(Authentication auth) {
        return ResponseEntity.ok("OK");
    }
}
