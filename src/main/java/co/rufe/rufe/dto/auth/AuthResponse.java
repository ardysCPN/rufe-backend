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
    private String type = "Bearer";

    @Schema(description = "ID del usuario autenticado", example = "501")
    private Long userId;

    @Schema(description = "Email del usuario autenticado", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "ID de la organización del usuario", example = "1")
    private Long organizacionId;

    @Schema(description = "Nombre de la organización del usuario", example = "MiEmpresaSAAS")
    private String organizacionNombre;

    @Schema(description = "ID del rol del usuario", example = "101")
    private Long rolId;

    @Schema(description = "Nombre del rol del usuario", example = "ADMIN")
    private String rolNombre;

    @Schema(description = "Lista de permisos asignados al usuario (ej. 'usuarios:crear', 'roles:leer')",
            example = "[\"organizaciones:leer\", \"usuarios:crear\"]")
    private List<String> permissions; // <-- NEW FIELD FOR PERMISSIONS
}