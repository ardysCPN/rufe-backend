package co.rufe.rufe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para la solicitud de inicio de sesión")
public class LoginRequest {

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email es inválido.")
    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Schema(description = "Contraseña del usuario", example = "PasswordSegura123")
    private String password;

}
