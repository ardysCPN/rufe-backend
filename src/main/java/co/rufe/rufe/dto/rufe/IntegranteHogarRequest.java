package co.rufe.rufe.dto.rufe;

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
public class IntegranteHogarRequest {

    @NotBlank(message = "El clienteId del integrante es obligatorio")
    private String clienteId;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer tipoDocumentoId;

    private String numeroDocumento;
    private LocalDate fechaNacimiento;

    private Integer parentescoId;
    private Integer generoId;
    private Integer pertenenciaEtnicaId;

    private String telefono;
    private Integer estadoPersonaId;
    private Boolean esFallecido;
    private String observacionSalud;
}
