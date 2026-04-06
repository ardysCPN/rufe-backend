package co.rufe.rufe.dto.evento;

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
public class EventoRealResponse {
    private Long id;
    private String clienteId;
    private String nombreEvento;
    private String tipoEvento;
    private LocalDate fechaEvento;
    private String departamento;
    private String municipio;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
