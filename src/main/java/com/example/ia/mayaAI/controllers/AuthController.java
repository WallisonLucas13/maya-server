package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.converters.UserConverter;
import com.example.ia.mayaAI.inputs.UserInput;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.responses.security.AuthResponse;
import com.example.ia.mayaAI.services.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Log4j2
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Local domain for development purposes
     */
    private static final String DOMAIN_LOCAL = "localhost";

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserInput userInput, HttpServletResponse response){
        UserModel userModel = UserConverter.convert(userInput);
        AuthResponse authResponse = authService.login(userModel);

        this.setCookieToResponse(authResponse, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserInput userInput, HttpServletResponse response){
        UserModel userModel = UserConverter.convert(userInput);
        AuthResponse authResponse = authService.login(userModel);

        this.setCookieToResponse(authResponse, response);
        return ResponseEntity.ok(authResponse);
    }

    private void setCookieToResponse(AuthResponse authResponse, HttpServletResponse response) {
        String currentDomain = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getHost();
        Cookie cookie = new Cookie("auth-token", authResponse.token());
        cookie.setHttpOnly(true);
        cookie.setPath(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
        cookie.setSecure(Boolean.TRUE.equals(!DOMAIN_LOCAL.equals(currentDomain)));

        log.info("Setting cookie with domain: {}, to path: {}", currentDomain, cookie.getPath());
        response.addCookie(cookie);
    }
}
