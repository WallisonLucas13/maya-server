package com.example.ia.mayaAI.services.security;

import com.example.ia.mayaAI.exceptions.AlreadyUserRegisteredException;
import com.example.ia.mayaAI.exceptions.InvalidCredentialsException;
import com.example.ia.mayaAI.exceptions.NotFoundUserException;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.common.MongoRepository;
import com.example.ia.mayaAI.repositories.common.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.responses.security.AuthResponse;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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

    /**
     * Registra um novo usuário
     * @param userModel Usuário a ser registrado
     * @return Resposta de autenticação com o token gerado
     */
    public AuthResponse register(UserModel userModel){
        this.userAlreadyRegistered(userModel.getUsername());
        userModel.setPassword(this.encodePassword(userModel.getPassword()));
        String token = this.jwtService.generateToken(userModel);
        mongoRepository.save(userModel);
        return new AuthResponse(token);
    }

    /**
     * Realiza o login do usuário
     * @param userModel Usuário a ser autenticado
     * @return Resposta de autenticação com o token gerado
     */
    public AuthResponse login(UserModel userModel){
        UserModel user = mongoRepository
                .findBy(FIND_BY_USERNAME, userModel.getUsername(), UserModel.class)
                .orElseThrow(() -> new NotFoundUserException("Credenciais inválidas, confira seu usuário e senha!"));

        if(passwordEncoder.matches(userModel.getPassword(), user.getPassword())){
            return new AuthResponse(jwtService.generateToken(user));
        }
        throw new InvalidCredentialsException("Credenciais inválidas, confira seu usuário e senha!");
    }

    /**
     * Codifica a senha do usuário
     * @param password Senha a ser codificada
     * @return Senha codificada
     */
    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    /**
     * Verifica se o usuário já está registrado
     * @param username Nome de usuário a ser verificado
     */
    private void userAlreadyRegistered(String username){
        mongoRepository.findBy(FIND_BY_USERNAME, username, UserModel.class)
                .ifPresent(user -> {
            throw new AlreadyUserRegisteredException("Cadastro não permitido, escolha outro nome de usuário!");
        });
    }
}
