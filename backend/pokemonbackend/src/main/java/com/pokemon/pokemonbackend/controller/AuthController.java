package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.RegisterRequest;
import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.pokemon.pokemonbackend.dto.LoginRequest;
import com.pokemon.pokemonbackend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AppUserRepository repo,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.repo = repo;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String jwt = jwtService.generateToken(
                (org.springframework.security.core.userdetails.UserDetails)
                        authentication.getPrincipal()
        );

        return ResponseEntity.ok(jwt);
    }

}

