package com.example.ia.mayaAI.services.security;

import com.example.ia.mayaAI.exceptions.AlreadyUserRegisteredException;
import com.example.ia.mayaAI.exceptions.InvalidCredentialsException;
import com.example.ia.mayaAI.exceptions.NotFoundUserException;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.common.MongoRepository;
import com.example.ia.mayaAI.repositories.common.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.responses.security.AuthResponse;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final MongoRepository mongoRepository;
    private static final String FIND_BY_USERNAME = "username";

    @Autowired
    public AuthService(MongoDatabase mongoDatabase) {
        this.mongoRepository = new MongoRepositoryImpl(mongoDatabase, "users");
    }

    public AuthResponse register(UserModel userModel){
        this.userAlreadyRegistered(userModel.getUsername());
        userModel.setPassword(this.encodePassword(userModel.getPassword()));
        String token = this.jwtService.generateToken(userModel);
        mongoRepository.save(userModel);
        return new AuthResponse(token);
    }

    public AuthResponse login(UserModel userModel){
        UserModel user = mongoRepository
                .findBy(FIND_BY_USERNAME, userModel.getUsername(), UserModel.class)
                .orElseThrow(() -> new NotFoundUserException("Credenciais inválidas, confira seu usuário e senha!"));

        if(passwordEncoder.matches(userModel.getPassword(), user.getPassword())){
            return new AuthResponse(jwtService.generateToken(user));
        }
        throw new InvalidCredentialsException("Credenciais inválidas, confira seu usuário e senha!");
    }

    public int getCookieExpirationTime() {
        return jwtService.calculateCookieExpirationTime();
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    private void userAlreadyRegistered(String username){
        mongoRepository.findBy(FIND_BY_USERNAME, username, UserModel.class)
                .ifPresent(user -> {
            throw new AlreadyUserRegisteredException("Cadastro não permitido, escolha outro nome de usuário!");
        });
    }
}
