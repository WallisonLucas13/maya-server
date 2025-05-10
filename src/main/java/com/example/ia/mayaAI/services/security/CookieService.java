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

    private static final String AUTHORIZATION_COOKIE_NAME = "Authorization";

    /**
     * Configura o cookie de autenticação na resposta
     * @param authResponse Resposta de autenticação
     * @param response Resposta HTTP
     */
    public void setAuthCookieToResponse(AuthResponse authResponse, HttpServletResponse response) {
        String currentDomain = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getHost();

        String cookieValue = authResponse.token();
        int maxAge = calculateCookieExpirationTime();
        boolean isSecure = !DOMAIN_LOCAL.equals(currentDomain);
        String sameSite = isSecure ? "None" : "Lax";

        String cookieHeader = String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; %s; SameSite=%s",
                AUTHORIZATION_COOKIE_NAME,
                cookieValue,
                maxAge,
                isSecure ? "Secure" : "",
                sameSite
        );
        response.setHeader("Set-Cookie", cookieHeader);
        log.info("Setting manual cookie with domain: {}, secure: {}, SameSite=None", currentDomain, isSecure);
    }

    /**
     * Remove o cookie de autenticação da resposta
     * @param response Resposta HTTP
     */
    public void removeAuthCookieToResponse(HttpServletResponse response) {
        String currentDomain = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getHost();
        boolean isSecure = !DOMAIN_LOCAL.equals(currentDomain);
        String sameSite = isSecure ? "None" : "Lax";

        String cookieHeader = String.format(
                "%s=; Path=/; Max-Age=0; HttpOnly; %s; SameSite=%s",
                AUTHORIZATION_COOKIE_NAME,
                isSecure ? "Secure" : "",
                sameSite
        );

        response.setHeader("Set-Cookie", cookieHeader);
        log.info("Removed cookie manually with path / and SameSite=None");
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
