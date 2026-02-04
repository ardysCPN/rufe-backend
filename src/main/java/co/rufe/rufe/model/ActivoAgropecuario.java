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
public class ActivoAgropecuario {
    private Long id;
    private Long registroRufeId;
    private String clienteId;
    private String registroRufeClienteId;

    private String sector; // AGRICOLA, PECUARIO
    private String tipoCultivo;
    private String unidadMedidaAgricola;
    private BigDecimal areaCantidadAgricola;

    private String especieAnimal;
    private Integer cantidadAnimal;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEliminacion;
}
