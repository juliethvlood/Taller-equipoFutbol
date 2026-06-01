package com.equipofutbol.equipofutbol_adso.service;


import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio para gestionar tokens JWT
 * El JWT (JSON Web Token) es un estándar abierto para transmitir información de
 * forma segura entre partes como un objeto JSON.
 * Este servicio se encarga de generar, validar y extraer información de los
 * tokens JWT utilizados
 * en la autenticación y autorización de usuarios en la aplicación. Proporciona
 * métodos para crear tokens con información personalizada,
 * verificar su validez, extraer datos específicos y refrescar tokens expirados.
 */

@Service
public class JwtService {
    /**
     * Llave secreta para generar la firma
     */
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Tiempo de expiracion del token en ms
     */
    @Value("${security.jwt.token-expiration}")
    private Long tokenExpiration;

    /**
     * Generar firma secreta a partir de nuestra secretKey del yaml
     * 
     * @return Firma secreta para generar el jwt
     */
    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generar un jwt
     * 
     * @param userId
     * @param rolId
     * @param userName
     * @return String jwt
     */
    public String generateToken(String userId, String rolId, String userName) {
        return Jwts.builder()
                .claims(Map.of("userId", userId, "rolId", rolId))
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSigninKey())
                .compact();
    }

    /**
     * Valida si el token es valido y si ha expirado o no
     * 
     * @param token
     * @return Boolean
     */
    public Boolean isTokenValid(String token) {
        try {
            // El parser intenta descifrar la firma con nuestra llave secreta
            Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            System.err.println("Token is invalid: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrae todos los claims (payload) del token
     * 
     * @param <T>
     * @param token
     * @param resolver
     * @return resolver del claim
     */
    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    /**
     * Extrae el propietario del token (nombre de usuario)
     * 
     * @param token
     * @return nombre de usuario
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
        // El subject es un claim reservado para el propietario del token, en este caso
        // el username
    }

    /**
     * Extraer el id del usuario
     * 
     * @param token
     * @return id del usuario
     */
    public String extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", String.class));
    }

    public String extractRolId(String token) {
        return extractClaims(token, claims -> claims.get("rolId", String.class));
    }

    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("rolId", String.class));
    }

    public String extractEmail(String token) {
        return extractUsername(token);
    }

    public String refreshToken(String token) throws Exception {
        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(getSigninKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new Exception("Token is expired " + e.getMessage());
        } catch (JwtException e) {
            throw new Exception("Token is invalid " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Server error " + e.getMessage());
        }

        return generateToken(claims.get("userId", String.class), claims.get("rolId", String.class), claims.getSubject());
    }
}
