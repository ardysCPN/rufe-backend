package co.rufe.rufe.dto;

import lombok.Data;

@Data
public class PlanificacionRequest {
    private Long eventoId;
    private Long registroRufeId;
    private Integer ayudaCatalogoId;
    private Double cantidad;
}
