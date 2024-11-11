package com.example.ia.mayaAI.configs;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.ia.mayaAI.exceptions.NotFoundUserException;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.UserRepository;
import com.example.ia.mayaAI.services.security.JwtService;
import com.example.ia.mayaAI.services.security.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        String username = "";

        if(token != null) {
            try {
                username = jwtService.validateToken(token);
            } catch (JWTDecodeException | SignatureVerificationException e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Acesso Negado!");
                return;

            } catch (TokenExpiredException e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Sessão expirada, faça login novamente!");
                return;
            }

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
