package co.rufe.rufe.model.catalogo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Departamento {
    private Integer id;
    private String nombre;
}
