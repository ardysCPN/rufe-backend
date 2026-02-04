package co.rufe.rufe.dto.rufe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRufeResponse {
    private Long id;
    private String clienteId;
    private String estado;
    private String mensaje;
    private LocalDateTime fechaRecepcion;
}
