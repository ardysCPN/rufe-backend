package co.rufe.rufe.model.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoAlojamientoActual {
    private Integer id;
    private String nombre;
}
