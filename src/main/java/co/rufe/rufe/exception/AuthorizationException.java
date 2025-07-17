package co.rufe.rufe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un usuario no tiene los permisos necesarios
 * para acceder a un recurso o realizar una acción.
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // Mapea a un código de estado HTTP 403 Forbidden
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
