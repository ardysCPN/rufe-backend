package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodegaInventario {
    private Long id;
    private Long organizacionId;
    private Integer ayudaCatalogoId;
    private BigDecimal cantidad;
    private LocalDateTime fechaActualizacion;
}
