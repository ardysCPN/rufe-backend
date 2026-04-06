package co.rufe.rufe.dto.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRealRequest {

    private String clienteId; // Opcional, generado si no viene

    @NotBlank(message = "El nombre del evento es obligatorio")
    private String nombreEvento;

    @NotBlank(message = "El tipo de evento es obligatorio")
    private String tipoEvento;

    @NotNull(message = "La fecha del evento es obligatoria")
    private LocalDate fechaEvento;

    @NotBlank(message = "El departamento es obligatorio")
    private String departamento;

    @NotBlank(message = "El municipio es obligatorio")
    private String municipio;

    private String descripcion;

    private String estado;
}
