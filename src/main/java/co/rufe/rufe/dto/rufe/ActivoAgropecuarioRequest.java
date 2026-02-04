package co.rufe.rufe.dto.rufe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivoAgropecuarioRequest {

    private String clienteId;

    @NotBlank(message = "El sector es obligatorio")
    @Pattern(regexp = "AGRICOLA|PECUARIO", message = "El sector debe ser AGRICOLA o PECUARIO")
    private String sector;

    // Agrícola
    private String tipoCultivo;
    private String unidadMedidaAgricola;
    private BigDecimal areaCantidadAgricola;

    // Pecuario
    private String especieAnimal;
    private Integer cantidadAnimal;
}
