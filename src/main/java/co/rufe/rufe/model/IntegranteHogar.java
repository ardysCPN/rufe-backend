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
public class IntegranteHogar {
    private Long id;
    private Long registroRufeId;
    private String clienteId;
    private String registroRufeClienteId; // FK temporal offline

    private String nombres;
    private String apellidos;

    private Integer tipoDocumentoId;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;

    private Integer parentescoId;
    private Integer generoId;
    private Integer pertenenciaEtnicaId;

    private String telefono;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEliminacion;
}
