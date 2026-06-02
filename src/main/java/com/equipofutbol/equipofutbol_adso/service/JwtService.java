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
 * Servicio para gestionar tokens JWT en la aplicación.
 * Se anota con @Service para que Spring lo detecte durante el escaneo de
 * componentes y lo registre como un bean en el contexto de la aplicación.
 * El JWT (JSON Web Token) es un estándar abierto (RFC 7519) que define una
 * forma compacta y autónoma de transmitir información entre partes como un
 * objeto JSON firmado digitalmente. Este servicio centraliza toda la lógica
 * relacionada con JWT: generación de tokens con claims personalizados (userId
 * y rolId), validación de la firma y expiración, extracción de claims
 * individuales y refresco de tokens expirados. La clave secreta y el tiempo
 * de expiración se inyectan desde el archivo application.yaml mediante @Value,
 * lo que permite cambiar la configuración sin modificar el código.
 */
@Service
public class JwtService {

    /**
     * Clave secreta codificada en Base64 que se utiliza para firmar los tokens
     * JWT. Se inyecta desde la propiedad "security.jwt.secret-key" del archivo
     * application.yaml. Esta clave debe mantenerse en secreto y ser lo
     * suficientemente larga (256 bits o más) para garantizar la seguridad de
     * la firma HMAC-SHA.
     */
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Tiempo de expiración del token en milisegundos. Se inyecta desde la
     * propiedad "security.jwt.token-expiration" del application.yaml.
     * Actualmente configurado en 600000 ms (10 minutos). Cuando el token
     * expira, el servidor lo rechaza y el cliente debe renovarlo usando el
     * endpoint /auth/refreshToken.
     */
    @Value("${security.jwt.token-expiration}")
    private Long tokenExpiration;

    /**
     * Genera una clave secreta HMAC-SHA a partir de la cadena Base64 almacenada
     * en secretKey. Decodifica el Base64 y crea una Key adecuada para el
     * algoritmo HMAC-SHA utilizando la biblioteca JJWT. Este método es interno
     * y se utiliza en todos los métodos que necesitan firmar o verificar tokens.
     * 
     * @return SecretKey lista para usar en operaciones de firma JWT.
     */
    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un nuevo token JWT con claims personalizados.
     * Construye un token que incluye: dos claims personalizados ("userId" y
     * "rolId") que permiten identificar al usuario y su rol sin consultar la
     * base de datos; el subject (username) como identificador estándar del
     * propietario del token; la fecha de emisión actual; la fecha de expiración
     * calculada como la hora actual más tokenExpiration milisegundos; y la
     * firma HMAC-SHA generada con la clave secreta. El resultado es un String
     * compacto listo para ser enviado al cliente.
     * 
     * @param userId   Identificador único del usuario (su ID en la BD).
     * @param rolId    Nombre del rol (ADMINISTRATOR o JUGADOR).
     * @param userName Nombre de usuario que será el subject del token.
     * @return Token JWT firmado como String.
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
     * Valida si un token JWT es válido verificando su firma y expiración.
     * Intenta parsear el token usando la clave secreta: si el parseo es exitoso,
     * significa que la firma es válida y el token no ha expirado, por lo que
     * retorna true. Si ocurre una JwtException (token malformado, firma
     * inválida o token expirado) o cualquier otra excepción, la captura, imprime
     * el error en la consola y retorna false. Este método es utilizado por
     * JwtValidationFilter para decidir si la petición debe continuar o rechazarse.
     * 
     * @param token Token JWT a validar.
     * @return true si el token es válido y no ha expirado, false en caso contrario.
     */
    public Boolean isTokenValid(String token) {
        try {
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
     * Extrae un valor específico del payload (claims) del token JWT aplicando
     * una función resolver. Este método genérico permite extraer cualquier claim
     * del token de forma flexible: se le pasa una función que recibe los Claims
     * y retorna el valor deseado. Por ejemplo, Claims::getSubject para el
     * username, o claims -> claims.get("userId", String.class) para el userId.
     * 
     * @param <T>     Tipo de dato del valor a extraer.
     * @param token   Token JWT del cual extraer los claims.
     * @param resolver Función que transforma los Claims en el tipo de retorno T.
     * @return Valor extraído de los claims según el resolver proporcionado.
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
     * Extrae el nombre de usuario (subject) del token JWT.
     * El subject es un claim reservado en el estándar JWT que identifica al
     * propietario del token. En esta aplicación, el subject contiene el username
     * del usuario autenticado, establecido durante la generación del token.
     * 
     * @param token Token JWT del cual extraer el username.
     * @return Nombre de usuario almacenado en el subject del token.
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extrae el ID del usuario desde el claim personalizado "userId" del token.
     * Este claim se estableció al generar el token y contiene el ID numérico
     * del usuario en la base de datos, permitiendo identificar al usuario sin
     * necesidad de consultas adicionales.
     * 
     * @param token Token JWT del cual extraer el userId.
     * @return ID del usuario como String.
     */
    public String extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extrae el ID del rol desde el claim personalizado "rolId" del token.
     * Contiene el nombre del rol (ADMINISTRATOR o JUGADOR) y es utilizado por
     * el filtro JWT para establecer el atributo "rolId" en la petición, que
     * luego puede ser consultado por SecurityContext para verificar permisos.
     * 
     * @param token Token JWT del cual extraer el rolId.
     * @return Nombre del rol como String.
     */
    public String extractRolId(String token) {
        return extractClaims(token, claims -> claims.get("rolId", String.class));
    }

    /**
     * Alias de extractRolId(). Extrae el rol del usuario desde el claim
     * personalizado "rolId". Se mantiene este método con nombre semánticamente
     * más claro para su uso en AuthService.refreshToken().
     * 
     * @param token Token JWT del cual extraer el role.
     * @return Nombre del rol como String.
     */
    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("rolId", String.class));
    }

    /**
     * Alias de extractUsername(). Se mantiene por compatibilidad en caso de
     * que en el futuro se agregue un claim de email. Actualmente retorna el
     * subject del token, que es el username.
     * 
     * @param token Token JWT del cual extraer el email.
     * @return Nombre de usuario (por ahora, el subject del token).
     */
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    /**
     * Refresca un token JWT generando uno nuevo a partir de los claims del
     * token existente. Parsea el token actual para extraer userId, rolId y
     * subject del payload. Si el token está expirado, captura la excepción
     * ExpiredJwtException y permite continuar con el refresco (los claims
     * aún son accesibles en la excepción). Si el token es inválido por otra
     * razón (firma incorrecta, malformado), lanza una excepción. En caso de
     * éxito, genera un nuevo token con los mismos claims pero con nueva fecha
     * de emisión y expiración, extendiendo la sesión del usuario.
     * 
     * @param token Token JWT a refrescar (puede estar expirado pero no inválido).
     * @return Nuevo token JWT con fecha de expiración renovada.
     * @throws Exception Si el token es inválido o ocurre un error inesperado.
     */
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
