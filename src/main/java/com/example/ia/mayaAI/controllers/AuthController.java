package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.converters.UserConverter;
import com.example.ia.mayaAI.inputs.UserInput;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.responses.AuthResponse;
import com.example.ia.mayaAI.services.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserInput userInput){
        UserModel userModel = UserConverter.convert(userInput);
        return ResponseEntity.ok(authService.register(userModel));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserInput userInput){
        UserModel userModel = UserConverter.convert(userInput);
        return ResponseEntity.ok(authService.login(userModel));
    }
}
