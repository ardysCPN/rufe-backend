package co.rufe.rufe.model.catalogo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Evento {
    private Integer id;
    private String nombre;
}
