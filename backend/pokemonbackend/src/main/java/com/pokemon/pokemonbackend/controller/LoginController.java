/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemon.pokemonbackend.controller;

import java.util.Optional;

import com.pokemon.pokemonbackend.model.AppUser;
import com.pokemon.pokemonbackend.repository.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private AppUserRepository AppUserRepository;
    private PasswordEncoder passwordEncoder;

    public LoginController(
            AuthenticationManager authenticationManager,
            AppUserRepository AppUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.AppUserRepository = AppUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/app-login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok("Login correcto");
    }
    
    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser requestAppUser) {
        AppUser appUser1 = new AppUser(null, requestAppUser.getUsername(), passwordEncoder.encode(requestAppUser.getPassword()), requestAppUser.getRole());
        AppUserRepository.save(appUser1);
        
        return ResponseEntity.ok(appUser1);
    }

    public record LoginRequest(String username, String password) {

    }

    // login prueba descartada
    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html#publish-authentication-manager-bean
    // https://projectai.in/projects/ad04048e-5a00-404d-ae8a-7cd13773f7de/tasks/d44fd118-291a-476b-ba3e-56f881f3b438?tab=task
    // https://github.com/spring-projects/spring-security-samples/tree/main/servlet/spring-boot/java/jwt/login
    //@PostMapping("/login")
    //public ResponseEntity<Authentication> login(@RequestBody LoginRequest loginRequest) {
        /*Authentication authenticationRequest
                = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse
                = this.authenticationManager.authenticate(authenticationRequest);
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //return ResponseEntity.noContent().build();
        //return ResponseEntity.ok(new LoginResponse(jwt));
        //return ResponseEntity.ok("vrverver");
        return ResponseEntity.ok(authentication);
    }*/

    
}
