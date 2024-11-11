package com.example.ia.mayaAI.services.security;

import com.example.ia.mayaAI.exceptions.AlreadyUserRegisteredException;
import com.example.ia.mayaAI.exceptions.InvalidCredentialsException;
import com.example.ia.mayaAI.exceptions.NotFoundUserException;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.UserRepository;
import com.example.ia.mayaAI.responses.AuthResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AuthResponse register(UserModel userModel){
        this.userAlreadyRegistered(userModel.getUsername());
        userModel.setPassword(this.encodePassword(userModel.getPassword()));
        String token = this.jwtService.generateToken(userModel);
        userRepository.save(userModel);
        return new AuthResponse(token);
    }

    @Transactional
    public AuthResponse login(UserModel userModel){
        UserModel user = userRepository.findByUsername(userModel.getUsername())
                .orElseThrow(() -> new NotFoundUserException("Usuário não encontrado"));

        if(passwordEncoder.matches(userModel.getPassword(), user.getPassword())){
            return new AuthResponse(jwtService.generateToken(user));
        }
        throw new InvalidCredentialsException("Credenciais inválidas, confira seu usuário e senha!");
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    private void userAlreadyRegistered(String username){
        userRepository.findByUsername(username).ifPresent(user -> {
            throw new AlreadyUserRegisteredException("Cadastro não permitido, escolha outro nome de usuário!");
        });
    }
}
