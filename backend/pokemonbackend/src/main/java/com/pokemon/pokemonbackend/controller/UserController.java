package com.pokemon.pokemonbackend.controller;


import com.pokemon.pokemonbackend.dto.UserResponseDTO;
import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import com.pokemon.pokemonbackend.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AppUserRepository appUserRepository;

    private final StorageService storageService;

    public UserController(AppUserRepository appUserRepository,
                          StorageService storageService) {
        this.appUserRepository = appUserRepository;
        this.storageService = storageService;
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

    @PostMapping("/me/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Only image files allowed");
        }

        String username = authentication.getName();

        AppUser user = appUserRepository
                .findByUsername(username)
                .orElseThrow();

        // Generamos nombre único
        String filename = "user_" + user.getId() + ".jpg";

        // Guardamos archivo
        storageService.store(file, filename);

        // Guardamos nombre en BD
        user.setProfileImage(filename);
        appUserRepository.save(user);

        return ResponseEntity.ok().body(
                Map.of(
                        "message", "Profile picture uploaded successfully",
                        "filename", filename
                )
        );
    }

    @GetMapping("/{id:\\d+}/profile-picture")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long id) {

        AppUser user = appUserRepository.findById(id)
                .orElseThrow();

        if (user.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource file = storageService.loadAsResource(user.getProfileImage());

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            Files.probeContentType(Path.of(file.getFile().getPath()))
                    ))
                    .body(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

