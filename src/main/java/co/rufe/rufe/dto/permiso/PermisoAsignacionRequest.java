package co.rufe.rufe.dto.permiso;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO para la asignación de permisos a un rol")
public class PermisoAsignacionRequest {

    @NotNull(message = "El ID del rol no puede ser nulo.")
    @Schema(description = "ID del rol al que se asignará el permiso", example = "101")
    private Long rolId;

    @NotNull(message = "El ID del ítem de menú no puede ser nulo.")
    @Schema(description = "ID del ítem de menú que se asignará como permiso", example = "1")
    private Long menuItemId;
}
