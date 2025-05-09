package com.example.ia.mayaAI.services.security;

import com.example.ia.mayaAI.responses.security.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Log4j2
@Service
public class CookieService {

    /**
     * Tempo de expiração do cookie em horas
     */
    @Value("${api.security.jwt.expiration-time-hours}")
    private int EXPIRATION_TIME_HOURS;

    /**
     * Dominio local para o ambiente de desenvolvimento
     */
    private static final String DOMAIN_LOCAL = "localhost";

    /**
     * Configura o cookie de autenticação na resposta
     * @param authResponse Resposta de autenticação
     * @param response Resposta HTTP
     */
    public void setAuthCookieToResponse(AuthResponse authResponse, HttpServletResponse response) {
        String currentDomain = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getHost();
        Cookie cookie = new Cookie("Authorization", authResponse.token());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(calculateCookieExpirationTime());
        cookie.setSecure(!DOMAIN_LOCAL.equals(currentDomain));
        response.addHeader("Set-Cookie", String.format("%s; SameSite=Lax", cookie.toString()));

        log.info("Setting cookie with domain: {}, to path: {}", currentDomain, cookie.getPath());
        response.addCookie(cookie);
    }

    /**
     * Remove o cookie de autenticação da resposta
     * @param response Resposta HTTP
     */
    public void removeAuthCookieToResponse(HttpServletResponse response) {
        String currentDomain = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getHost();
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(!DOMAIN_LOCAL.equals(currentDomain));

        log.info("Removing cookie with path: {}", cookie.getPath());
        response.addCookie(cookie);
    }

    /**
     * Calcula o tempo de expiração do cookie em segundos
     * @return Tempo de expiração do cookie em segundos
     */
    private int calculateCookieExpirationTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime expirationTime = now.plusHours(EXPIRATION_TIME_HOURS);
        return (int) (expirationTime.toEpochSecond() - now.toEpochSecond());
    }
}
