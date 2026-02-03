package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.LoginRequest;
import com.pokemon.pokemonbackend.dto.RegisterRequest;
import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
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

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public AuthController(
            AppUserRepository repo,
            PasswordEncoder encoder,
            AuthenticationManager authManager
    ) {
        this.repo = repo;
        this.encoder = encoder;
        this.authManager = authManager;
    }

    // -------- REGISTRO --------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {

        if (repo.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        AppUser user = new AppUser(
                req.getUsername(),
                encoder.encode(req.getPassword()),
                "ROLE_USER"
        );

        repo.save(user);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    // -------- LOGIN --------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {

        Authentication auth = new UsernamePasswordAuthenticationToken(
                req.getUsername(),
                req.getPassword()
        );

        authManager.authenticate(auth);

        return ResponseEntity.ok("Credenciales válidas");
    }
}
