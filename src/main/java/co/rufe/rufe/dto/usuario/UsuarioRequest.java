package co.rufe.rufe.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para la creación/actualización de un usuario")
public class UsuarioRequest {

    @NotBlank(message = "El nombre completo no puede estar vacío.")
    @Size(min = 5, max = 200, message = "El nombre completo debe tener entre 5 y 200 caracteres.")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez Gómez")
    private String nombreCompleto;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email es inválido.")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres.")
    @Schema(description = "Email único del usuario", example = "juan.perez@example.com")
    private String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @Schema(description = "Contraseña del usuario", example = "PasswordSegura123")
    private String password;

    @NotNull(message = "El ID del rol no puede ser nulo.")
    @Schema(description = "ID del rol asignado al usuario", example = "101")
    private Long rolId;

    @Schema(description = "Estado de actividad del usuario", example = "true")
    private Boolean activo; // Opcional, si no se envía, el DAO puede usar el default de la BD
}
