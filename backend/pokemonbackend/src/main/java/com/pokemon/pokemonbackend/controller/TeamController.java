package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.TeamResponseDTO;
import com.pokemon.pokemonbackend.model.Team;
import com.pokemon.pokemonbackend.model.User;
import com.pokemon.pokemonbackend.repository.TeamRepository;
import com.pokemon.pokemonbackend.repository.UserRepository;
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
    private final UserRepository userRepository;

    public TeamController(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    // CREATE (team del usuario autenticado)
    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(
            @RequestBody Team team,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User authUser
    ) {
        User user = userRepository.findByUsername(authUser.getUsername())
                .orElseThrow();

        team.setUser(user);
        Team saved = teamRepository.save(team);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TeamResponseDTO(
                        saved.getId(),
                        saved.getName(),
                        saved.getFormat(),
                        user.getId()
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

        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                teamRepository.findByUser(user)
                        .stream()
                        .map(t -> new TeamResponseDTO(
                                t.getId(),
                                t.getName(),
                                t.getFormat(),
                                user.getId()
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
