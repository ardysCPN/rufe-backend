package co.rufe.rufe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando la autenticación del usuario falla
 * (ej. credenciales incorrectas, usuario inactivo).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // Mapea a un código de estado HTTP 401 Unauthorized
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
