package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.converters.UserConverter;
import com.example.ia.mayaAI.inputs.UserInput;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.responses.security.AuthResponse;
import com.example.ia.mayaAI.services.security.AuthService;
import com.example.ia.mayaAI.services.security.CookieService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserInput userInput, HttpServletResponse response){
        UserModel userModel = UserConverter.convert(userInput);
        AuthResponse authResponse = authService.register(userModel);

        cookieService.setAuthCookieToResponse(authResponse, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserInput userInput, HttpServletResponse response){
        UserModel userModel = UserConverter.convert(userInput);
        AuthResponse authResponse = authService.login(userModel);

        cookieService.setAuthCookieToResponse(authResponse, response);
        return ResponseEntity.ok(authResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response){
        cookieService.removeAuthCookieToResponse(response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
