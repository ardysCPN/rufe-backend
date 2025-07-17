package co.rufe.rufe.dto.organizacion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para la creación/actualización de una organización")
public class OrganizacionRequest {

    @NotBlank(message = "El nombre de la organización no puede estar vacío.")
    @Size(min = 3, max = 255, message = "El nombre de la organización debe tener entre 3 y 255 caracteres.")
    @Schema(description = "Nombre único de la organización", example = "MiEmpresaSAAS")
    private String nombreOrganizacion;

    @Schema(description = "Estado de actividad de la organización", example = "true")
    private Boolean activa; // Opcional, si no se envía, el DAO puede usar el default de la BD
}
