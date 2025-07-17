package co.rufe.rufe.dto.rol;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para la creación/actualización de un rol")
public class RolRequest {

    @NotBlank(message = "El nombre del rol no puede estar vacío.")
    @Size(min = 2, max = 100, message = "El nombre del rol debe tener entre 2 y 100 caracteres.")
    @Schema(description = "Nombre único del rol dentro de la organización", example = "ADMIN")
    private String nombreRol;

    @Schema(description = "Descripción del rol", example = "Rol con permisos administrativos completos.")
    private String descripcion;
    
    @Schema(description = "ID de la organización a la que pertenece el rol", example = "1")
    private Long organizacionId;
}
