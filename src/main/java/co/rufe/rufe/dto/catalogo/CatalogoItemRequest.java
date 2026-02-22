package co.rufe.rufe.dto.catalogo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear o actualizar un item de catálogo")
public class CatalogoItemRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del item de catálogo", example = "CEDULA DE CIUDADANIA")
    private String nombre;
}
