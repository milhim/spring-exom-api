package com.milhim.ecommerce.ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.milhim.ecommerce.ecommerce.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    private Algorithm algorithm;

    private static final String USERNAME_KEY = "USERNAME";
    private static final String EMAIL_KEY = "EMAIL";

    @PostConstruct
    public void postConstructed() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(LocalUser localUser) {
        return JWT.create()
                .withClaim(USERNAME_KEY, localUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() * (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }


    public String generateVerificationJWT(LocalUser localUser) {
        return JWT.create()
                .withClaim(EMAIL_KEY, localUser.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() * (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Gets Username out of jwt token
     *
     * @param token The JWT decoded
     * @return the username stored inside
     */
    public String getUsername(String token) {
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

}
