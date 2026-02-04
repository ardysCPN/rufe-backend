package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BienAfectado {
    private Long id;
    private Long registroRufeId;
    private String clienteId;
    private String registroRufeClienteId;

    private Integer tipoBienId;
    private Integer formaTenenciaBienId;
    private Integer estadoBienId;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEliminacion;
}
