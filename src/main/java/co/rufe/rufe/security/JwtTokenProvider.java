package co.rufe.rufe.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Importar SignatureException
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException; // Para compatibilidad con tu error
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // Usar javax.crypto.SecretKey
import java.util.Date;

import java.util.stream.Collectors;
import java.util.Collection; // Importar Collection

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
    // Modificado para aceptar Authentication y Collection<GrantedAuthority>
    public String generateToken(Authentication authentication, Collection<? extends GrantedAuthority> authorities) {
        String email = authentication.getName(); // El subject es el email

        // Convertir las autoridades a una cadena separada por comas
        String authoritiesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationMs);

        // Claims personalizados (opcional, si quieres mantener userId, organizacionId,
        // etc. explícitos)
        // Puedes obtener estos del UserDetails si tu CustomUserDetailsService devuelve
        // un UserDetails personalizado
        // que contenga estos campos. Por simplicidad, solo incluimos las "authorities"
        // como una claim.
        // Claims personalizados
        // CORRECCIÓN: Guardar authorities directamente como String, sin Map anidado.
        // Esto permite que JwtAuthenticationFilter lo lea como un simple claim de
        // texto.
        return Jwts.builder()
                .subject(email)
                .claim("authorities", authoritiesString) // Guardar como String plano: "ROLE_ADMIN,perm1,perm2"
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Obtiene el email (subject) del JWT
    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // Obtiene el ID de la organización del JWT
    // NOTA: Si el organizacionId no se guarda como un claim en generateToken,
    // este método no podrá extraerlo. Considera añadirlo a los claims si es
    // necesario.
    public Long getOrganizacionIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Object organizacionIdObj = claims.get("organizacionId"); // Asume que "organizacionId" es un claim
        if (organizacionIdObj instanceof Number) {
            return ((Number) organizacionIdObj).longValue();
        }
        // Si no se encuentra o no es un número, podrías lanzar una excepción o devolver
        // null
        log.warn("Claim 'organizacionId' no encontrado o no es un número válido en el token JWT: {}",
                organizacionIdObj);
        return null; // O lanza una IllegalArgumentException
    }

    // Obtiene las autoridades (roles y permisos) del JWT como una cadena
    public String getAuthoritiesFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("authorities", String.class); // Recuperar la claim "authorities"
    }

    // Valida el token JWT
    // Simplificado para usar la API de JJWT 0.12.x y manejar excepciones
    public boolean validateToken(String token) { // Ya no necesita UserDetails aquí para la validación básica
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Firma JWT inválida: {}", e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("JWT con firma inválida.");
        } catch (MalformedJwtException e) {
            log.error("JWT malformado: {}", e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("JWT malformado o incorrecto.");
        } catch (ExpiredJwtException e) {
            log.error("JWT expirado: {}", e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("JWT expirado.");
        } catch (UnsupportedJwtException e) {
            log.error("JWT no soportado: {}", e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("JWT no soportado.");
        } catch (IllegalArgumentException e) {
            log.error("La cadena JWT está vacía o es inválida: {}", e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("JWT inválido o vacío.");
        }
    }

    public String getUsername(String token) {
        // En este contexto, el "username" es el subject del JWT (generalmente el email)
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}