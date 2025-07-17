package co.rufe.rufe.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey; // ¡Importante! Usar javax.crypto.SecretKey

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${application.jwt.secret}")
    private String jwtSecret;

    @Value("${application.jwt.expiration}")
    private long jwtExpirationMs;

    // Método para obtener la SecretKey
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera el token JWT
    public String generateToken(Long organizacionId, Long userId, String rolName, String email) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationMs);

        // Claims personalizados para incluir en el JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("organizacionId", organizacionId);
        claims.put("userId", userId);
        claims.put("rol", rolName);
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims) // Añade todos los claims personalizados
                .setSubject(email) // El "subject" del token
                .issuedAt(currentDate) // Fecha de emisión
                .expiration(expireDate) // Fecha de expiración
                .signWith(getSigningKey()) // Firma el token
                .compact(); // Construye y compacta el JWT
    }

    // Obtiene el email (subject) del JWT
    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parser() // Inicia el parser
                .verifyWith(getSigningKey()) // Verifica la firma con la clave secreta
                .build() // Construye el parser final
                .parseSignedClaims(token) // Parsea los claims firmados
                .getPayload(); // Obtiene el payload (claims)
        return claims.getSubject();
    }

    // Obtiene el ID de la organización del JWT
    public Long getOrganizacionIdFromJwt(String token) {
        Claims claims = Jwts.parser() // Inicia el parser
                .verifyWith(getSigningKey()) // Verifica la firma
                .build() // Construye el parser
                .parseSignedClaims(token) // Parsea los claims firmados
                .getPayload(); // Obtiene el payload (claims)

        Object organizacionIdObj = claims.get("organizacionId");
        if (organizacionIdObj instanceof Number) {
            return ((Number) organizacionIdObj).longValue();
        }
        log.error("El claim 'organizacionId' no es un número válido: {}", organizacionIdObj);
        throw new IllegalArgumentException("Claim 'organizacionId' inválido en el token JWT.");
    }

    // Valida el token JWT
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch(SecurityException | MalformedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT token compact of handler are invalid.");
        }
    }
}