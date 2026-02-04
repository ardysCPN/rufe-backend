package co.rufe.rufe.dto.rufe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRufeCreateRequest {

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotNull(message = "El tipo de evento es obligatorio")
    private Long tipoEventoId;

    @NotBlank(message = "El clienteId (UUID) es obligatorio")
    private String clienteId;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDateTime fechaRegistro;

    private Integer tipoUbicacionBienId;
    private String corregimiento;
    private String veredaSectorBarrio;
    private String direccion;

    private Integer tipoAlojamientoActualId;
    private String lugarHabitualResidencia;
    private Boolean evacuadoFueraResidencia;
    private String observaciones;
    private String voBoCmgrd;

    @Valid
    @NotEmpty(message = "El registro debe tener al menos un integrante")
    private List<IntegranteHogarRequest> integrantes;

    @Valid
    private List<BienAfectadoRequest> bienesAfectados;

    @Valid
    private List<ActivoAgropecuarioRequest> activosAgropecuarios;
}
