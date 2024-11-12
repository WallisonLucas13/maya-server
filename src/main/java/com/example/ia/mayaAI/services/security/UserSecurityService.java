package com.example.ia.mayaAI.services.security;

import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas, confira seu usuário e senha!"));

        return new User(userModel.getUsername(), userModel.getPassword(), userModel.getAuthorities());
    }
}
