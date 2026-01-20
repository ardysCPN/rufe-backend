package co.rufe.rufe.model.catalogo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Municipio {
    private Integer id;
    private String nombre;
    private Integer departamentoId;
}
