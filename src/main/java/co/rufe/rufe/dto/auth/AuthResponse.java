package co.rufe.rufe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    @Schema(description = "Token de autenticación JWT", example = "eyJhbGciOiJIUzI1NiI...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    @Builder.Default
    private String type = "Bearer";

    @Schema(description = "Objeto con datos del usuario")
    private UserAuthDTO user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAuthDTO {
        private Long id;
        private String nombre;
        private String rol;
        private Long rolId;
        private Long organizacionId; // Mantener para el frontend saber dónde está
    }
}