package com.example.ia.mayaAI.configs;

import com.example.ia.mayaAI.exceptions.NotFoundUserException;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.UserRepository;
import com.example.ia.mayaAI.services.security.JwtService;
import com.example.ia.mayaAI.services.security.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        String username = jwtService.validateToken(token);

        if(username != null) {
            UserModel userModel = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundUserException("Usuário não encontrado"));

            var authentication = new UsernamePasswordAuthenticationToken(userModel, null, userModel.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        String headerToken = request.getHeader("Authorization");
        if(headerToken == null) return null;
        return headerToken.replace("Bearer ", "");
    }
}
