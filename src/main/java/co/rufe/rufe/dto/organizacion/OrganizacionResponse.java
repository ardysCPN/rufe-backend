package co.rufe.rufe.dto.organizacion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO para la respuesta de una organización")
public class OrganizacionResponse {

    @Schema(description = "ID único de la organización", example = "1")
    private Long id;

    @Schema(description = "Nombre de la organización", example = "MiEmpresaSAAS")
    private String nombreOrganizacion;

    @Schema(description = "NIT de la organización", example = "800.123.456-7")
    private String nit;

    @Schema(description = "Dirección de la organización", example = "Calle 14 # 8-30")
    private String direccion;

    @Schema(description = "Teléfono de la organización", example = "3001234567")
    private String telefono;

    @Schema(description = "Estado de actividad de la organización", example = "true")
    private Boolean activa;

    @Schema(description = "Fecha de creación de la organización")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última actualización de la organización")
    private LocalDateTime fechaActualizacion;
}
