package co.rufe.rufe.dto.catalogo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatalogoMunicipioResponse {
    private Integer id;
    private String nombre;
    private Integer departamentoId; // Si aplica
}