package com.pokemon.pokemonbackend.controller;

import com.pokemon.pokemonbackend.dto.TeamResponseDTO;
import com.pokemon.pokemonbackend.model.Team;
import com.pokemon.pokemonbackend.model.User;
import com.pokemon.pokemonbackend.repository.TeamRepository;
import com.pokemon.pokemonbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // CREATE
    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(
            @RequestParam Long userId,
            @RequestBody Team team
    ) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

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

    // READ ALL
    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {

        List<TeamResponseDTO> response = teamRepository.findAll()
                .stream()
                .map(t -> new TeamResponseDTO(
                        t.getId(),
                        t.getName(),
                        t.getFormat(),
                        t.getUser().getId()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    // READ BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsByUser(@PathVariable Long userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<TeamResponseDTO> response = teamRepository.findByUser(user)
                .stream()
                .map(t -> new TeamResponseDTO(
                        t.getId(),
                        t.getName(),
                        t.getFormat(),
                        userId
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {

        if (!teamRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        teamRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}