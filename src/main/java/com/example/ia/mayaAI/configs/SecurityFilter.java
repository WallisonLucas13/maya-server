package com.example.ia.mayaAI.configs;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.ia.mayaAI.exceptions.NotFoundUserException;
import com.example.ia.mayaAI.models.UserModel;
import com.example.ia.mayaAI.repositories.common.MongoRepository;
import com.example.ia.mayaAI.repositories.common.impl.MongoRepositoryImpl;
import com.example.ia.mayaAI.services.security.JwtService;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Log4j2
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MongoRepository mongoRepository;

    /**
     * Caminhos que não precisam de autenticação
     */
    @Value("${api.access.control.allow.paths}")
    private String allowedPaths;

    /**
     * Chave para encontrar o usuário no banco de dados
     */
    private static final String FIND_BY_USERNAME = "username";

    /**
     * Nome do token de autorização
     */
    private static final String AUTHORIZATION_TOKEN_NAME = "Authorization";

    /**
     * Prefixo do token de autorização
     */
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

    @Autowired
    public SecurityFilter(JwtService jwtService, MongoDatabase mongoDatabase) {
        this.jwtService = jwtService;
        this.mongoRepository = new MongoRepositoryImpl(mongoDatabase, "users");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenFromHeader = this.recoverTokenFromHeader(request);
        String tokenFromCookie = this.recoverTokenFromCookie(request);

        if(Objects.nonNull(tokenFromHeader) || Objects.nonNull(tokenFromCookie)) {
            try {
                validateTokenAndSetAuthentication(ObjectUtils.firstNonNull(tokenFromHeader, tokenFromCookie));
            } catch (JWTDecodeException | SignatureVerificationException e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Acesso Negado!");
                return;

            } catch (TokenExpiredException e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Sessão expirada, faça login novamente!");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith(allowedPaths);
    }

    private void validateTokenAndSetAuthentication(String token)
            throws JWTDecodeException, SignatureVerificationException, TokenExpiredException {
        String username = jwtService.validateToken(token);
        UserModel userModel = mongoRepository
                .findBy(FIND_BY_USERNAME, username, UserModel.class)
                .orElseThrow(() -> new NotFoundUserException("Usuário não encontrado"));

        var authentication = new UsernamePasswordAuthenticationToken(userModel, null, userModel.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recoverTokenFromHeader(HttpServletRequest request){
        String token = request.getHeader(AUTHORIZATION_TOKEN_NAME);
        if(token == null) return null;
        return token.replace(AUTHORIZATION_TOKEN_PREFIX, StringUtils.EMPTY);
    }

    private String recoverTokenFromCookie(HttpServletRequest request){
        return Arrays.stream(ObjectUtils.defaultIfNull(request.getCookies(), new Cookie[0]))
                .filter(cookie -> cookie.getName().equals(AUTHORIZATION_TOKEN_NAME))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
