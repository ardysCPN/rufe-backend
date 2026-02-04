package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoReal {
    private Long id;
    private Long organizacionId;
    private String clienteId; // UUID
    private String nombreEvento;
    private String tipoEvento; // "Derrumbe", "Inundacion", etc.
    private LocalDate fechaEvento;
    private String departamento;
    private String municipio;
    private String descripcion;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEliminacion;
}
