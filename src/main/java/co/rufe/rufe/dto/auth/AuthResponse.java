package co.rufe.rufe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para la respuesta de autenticación")
public class AuthResponse {

    @Schema(description = "Token de autenticación JWT", example = "eyJhbGciOiJIUzI1NiI...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID del usuario autenticado", example = "501")
    private Long userId;

    @Schema(description = "Email del usuario autenticado", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Nombre de la organización del usuario", example = "MiEmpresaSAAS")
    private String organizacionNombre;

    @Schema(description = "ID del rol del usuario", example = "101")
    private Long rolId;

    @Schema(description = "Nombre del rol del usuario", example = "ADMIN")
    private String rolNombre;
}
