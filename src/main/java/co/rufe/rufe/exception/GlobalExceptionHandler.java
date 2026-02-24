package co.rufe.rufe.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import co.rufe.rufe.util.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j // Para logging
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        /**
         * Maneja excepciones de recursos no encontrados (HTTP 404).
         * Mensaje diciente: "El recurso solicitado no fue encontrado."
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
                        WebRequest request) {
                log.warn("Recurso no encontrado: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false),
                                null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        /**
         * Maneja excepciones de recursos duplicados (HTTP 409 Conflict).
         * Mensaje diciente: "El recurso que intenta crear ya existe."
         */
        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex,
                        WebRequest request) {
                log.warn("Conflicto de recurso: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false),
                                null);
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        /**
         * Maneja violaciones de reglas de negocio (HTTP 409 Conflict o 400 Bad
         * Request).
         */
        @ExceptionHandler(BusinessRuleException.class)
        public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
                log.warn("Regla de negocio violada: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Regla de Negocio",
                                ex.getMessage(),
                                request.getDescription(false),
                                null);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja excepciones de autenticación (HTTP 401 Unauthorized).
         * Mensaje diciente: "Credenciales inválidas o token no proporcionado/expirado."
         */
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,
                        WebRequest request) {
                log.warn("Fallo de autenticación: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.UNAUTHORIZED.value(),
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false),
                                null);
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        /**
         * Maneja excepciones de autorización (HTTP 403 Forbidden).
         * Mensaje diciente: "No tiene permisos para realizar esta acción."
         */
        @ExceptionHandler(AuthorizationException.class)
        public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException ex,
                        WebRequest request) {
                log.warn("Fallo de autorización: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.FORBIDDEN.value(),
                                HttpStatus.FORBIDDEN.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false),
                                null);
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        /**
         * Maneja excepciones de argumentos inválidos (ej. reglas de negocio) (HTTP 400
         * Bad Request).
         * Mensaje diciente: "La solicitud contiene datos inválidos."
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                        WebRequest request) {
                log.warn("Argumento inválido: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false),
                                null);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja excepciones relacionadas con problemas de base de datos de Spring
         * (DataAccessException).
         * Esto incluye errores de conexión, SQL malformado, etc.
         * Mensaje diciente: "Error en la operación de base de datos."
         */
        @ExceptionHandler(DataAccessException.class)
        public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex, WebRequest request) {
                log.error("Error de acceso a datos: {}", ex.getMessage(), ex);
                String message = "Ha ocurrido un error en la base de datos. Por favor, inténtelo de nuevo más tarde.";
                if (ex instanceof DuplicateKeyException) {
                        message = "El registro que intenta crear ya existe (conflicto de clave única).";
                }
                // Puedes añadir más lógica para otros tipos de DataAccessException si es
                // necesario.

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(), // Podría ser 409 para DuplicateKey, pero 500
                                                                          // para genéricos
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                message,
                                request.getDescription(false),
                                null);
                // Para DuplicateKeyException, se podría retornar HttpStatus.CONFLICT.
                // Pero dado que DataAccessException es muy genérica, un 500 es un buen
                // fallback.
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Sobrescribe el método para manejar `MethodArgumentNotValidException` (errores
         * de @Valid) (HTTP 400 Bad Request).
         * Mensaje diciente: "Los datos de entrada no son válidos."
         */
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex, HttpHeaders headers,
                        org.springframework.http.HttpStatusCode status,
                        WebRequest request) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                log.warn("Errores de validación de argumentos: {}", errors);

                // Puedes optar por devolver todos los errores de campo en la respuesta si lo
                // deseas,
                // o un mensaje más general como el actual para el usuario final.
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Validación de datos fallida",
                                "Los datos de entrada no son válidos. Revise los campos.",
                                request.getDescription(false),
                                errors);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * Manejador genérico para cualquier otra excepción no controlada (HTTP 500
         * Internal Server Error).
         * Mensaje diciente: "Ha ocurrido un error inesperado."
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
                log.error("Ocurrió un error inesperado: {}", ex.getMessage(), ex); // Loguea la stack trace completa
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "Ha ocurrido un error inesperado. Por favor, inténtelo de nuevo más tarde o contacte a soporte.",
                                request.getDescription(false),
                                null);
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
