package co.rufe.rufe.dto.rufe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BienAfectadoRequest {

    private String clienteId;

    @NotNull(message = "El tipo de bien es obligatorio")
    private Integer tipoBienId;

    private Integer formaTenenciaBienId;
    private Integer estadoBienId;
}
