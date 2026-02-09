package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.TeamResponseDTO;
import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.model.Team;
import com.pokemon.pokemonbackend.repository.TeamRepository;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamRepository teamRepository;
    private final AppUserRepository appUserRepository;

    public TeamController(TeamRepository teamRepository, AppUserRepository appUserRepository) {
        this.teamRepository = teamRepository;
        this.appUserRepository = appUserRepository;
    }

    // CREATE (team del usuario autenticado)
    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(
            @RequestBody Team team,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User authUser
    ) {
        AppUser appUser = appUserRepository.findByUsername(authUser.getUsername())
                .orElseThrow();

        team.setUser(appUser);
        Team saved = teamRepository.save(team);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TeamResponseDTO(
                        saved.getId(),
                        saved.getName(),
                        saved.getFormat(),
                        appUser.getId()
                ));
    }

    // READ ALL (opcional)
    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        return ResponseEntity.ok(
                teamRepository.findAll()
                        .stream()
                        .map(t -> new TeamResponseDTO(
                                t.getId(),
                                t.getName(),
                                t.getFormat(),
                                t.getUser().getId()
                        ))
                        .toList()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<List<TeamResponseDTO>> getMyTeams(Principal principal) {

        AppUser appUser = appUserRepository
                .findByUsername(principal.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                teamRepository.findByUser(appUser)
                        .stream()
                        .map(t -> new TeamResponseDTO(
                                t.getId(),
                                t.getName(),
                                t.getFormat(),
                                appUser.getId()
                        ))
                        .toList()
        );
    }


    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        if (!teamRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        teamRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
