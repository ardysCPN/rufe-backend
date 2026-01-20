package co.rufe.rufe.model.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PertenenciaEtnica {
    private Integer id;
    private String nombre;
}
