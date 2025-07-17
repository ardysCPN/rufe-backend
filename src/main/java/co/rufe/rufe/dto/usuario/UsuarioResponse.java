package co.rufe.rufe.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO para la respuesta de un usuario")
public class UsuarioResponse {

    @Schema(description = "ID único del usuario", example = "501")
    private Long id;

    @Schema(description = "ID de la organización a la que pertenece el usuario", example = "1")
    private Long organizacionId;

    @Schema(description = "ID del rol asignado al usuario", example = "101")
    private Long rolId;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez Gómez")
    private String nombreCompleto;

    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Estado de actividad del usuario", example = "true")
    private Boolean activo;

    @Schema(description = "Fecha de creación del usuario")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última actualización del usuario")
    private LocalDateTime fechaActualizacion;
}
