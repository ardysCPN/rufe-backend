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
public class PlanificacionEntrega {
    private Long id;
    private Long organizacionId;
    private Long eventoId;
    private Long registroRufeId;
    private Integer ayudaCatalogoId;
    private Double cantidad;
    private String estado; // PENDIENTE, ENTREGADO, CANCELADO
    private LocalDateTime fechaCreacion;
    
    // Auxiliar para UI
    private String nombreBeneficiario;
    private String nombreArticulo;
}
