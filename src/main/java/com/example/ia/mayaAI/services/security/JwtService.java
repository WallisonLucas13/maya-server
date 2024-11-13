package com.example.ia.mayaAI.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.ia.mayaAI.models.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;

@Service
public class JwtService {

    @Value("${api.security.jwt.secret-key}")
    private String SECRET_KEY;
    @Value("${api.security.jwt.expiration-time-hours}")
    private int EXPIRATION_TIME_HOURS;
    private final String JWT_ISSUER = "maya-ai-security";

    public String generateToken(UserModel userModel) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(JWT_ISSUER)
                    .withSubject(userModel.getUsername())
                    .withExpiresAt(calculateExpirationDate())
                    .sign(algorithm);

        }catch(JWTCreationException e){
            throw new RuntimeException("Error creating JWT token");
        }
    }

    public String validateToken(String token) throws JWTDecodeException, TokenExpiredException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.require(algorithm)
                .withIssuer(JWT_ISSUER)
                .build()
                .verify(token)
                .getSubject();

    }

    private Instant calculateExpirationDate() {
        return Instant.from(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
                .plusHours(EXPIRATION_TIME_HOURS)
                .toLocalDateTime());
    }
}
