package com.example.ia.mayaAI.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.ia.mayaAI.models.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
public class JwtService {

    @Value("${api.security.jwt.secret-key}")
    private String SECRET_KEY;
    @Value("${api.security.jwt.expiration-time-hours}")
    private int EXPIRATION_TIME_HOURS;
    private static final String JWT_ISSUER = "maya-ai-security";

    public String generateToken(UserModel userModel) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(JWT_ISSUER)
                    .withSubject(userModel.getUsername())
                    .withExpiresAt(calculateTokenExpirationDate())
                    .sign(algorithm);

        }catch(JWTCreationException e){
            throw new RuntimeException("Error creating JWT token");
        }
    }

    public int calculateCookieExpirationTime() {
        return EXPIRATION_TIME_HOURS * 60 * 60;
    }

    public String validateToken(String token) throws JWTDecodeException, TokenExpiredException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.require(algorithm)
                .withIssuer(JWT_ISSUER)
                .build()
                .verify(token)
                .getSubject();

    }

    private Date calculateTokenExpirationDate() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
                .plusHours(EXPIRATION_TIME_HOURS);
        return Date.from(zonedDateTime.toInstant());
    }
}
