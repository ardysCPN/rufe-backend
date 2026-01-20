package co.rufe.rufe.dto.catalogo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representación genérica de un ítem de catálogo (ID y Nombre)")
public class CatalogoItemResponse {
    @Schema(description = "ID único del ítem de catálogo", example = "1")
    private Integer id;
    @Schema(description = "Nombre o descripción del ítem de catálogo", example = "Urbano")
    private String nombre;
}
