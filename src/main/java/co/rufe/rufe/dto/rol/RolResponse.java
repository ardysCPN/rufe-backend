package co.rufe.rufe.dto.rol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO para la respuesta de un rol")
public class RolResponse {

    @Schema(description = "ID único del rol", example = "101")
    private Long id;

    @Schema(description = "ID de la organización a la que pertenece el rol", example = "1")
    private Long organizacionId;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombreRol;

    @Schema(description = "Descripción del rol", example = "Rol con permisos administrativos completos.")
    private String descripcion;

    @Schema(description = "Fecha de creación del rol")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última actualización del rol")
    private LocalDateTime fechaActualizacion;
}
