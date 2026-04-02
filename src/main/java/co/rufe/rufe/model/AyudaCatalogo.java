package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AyudaCatalogo {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
}
