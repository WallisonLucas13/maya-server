package com.example.ia.mayaAI.services.security;

import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.MongoRepository;
import com.example.ia.mayaAI.repositories.impl.MongoRepositoryImpl;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService implements UserDetailsService {

    private final MongoRepository mongoRepository;
    private static final String FIND_BY_USERNAME = "username";

    @Autowired
    public UserSecurityService(MongoDatabase mongoDatabase) {
        this.mongoRepository = new MongoRepositoryImpl(mongoDatabase, "users");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = mongoRepository.findBy(FIND_BY_USERNAME, username, UserModel.class)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas, confira seu usuário e senha!"));

        return new User(userModel.getUsername(), userModel.getPassword(), userModel.getAuthorities());
    }
}
