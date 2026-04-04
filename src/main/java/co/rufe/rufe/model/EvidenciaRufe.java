package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenciaRufe {
    private Long id;
    private Long registroRufeId;
    private String tipoEvidencia; // e.g. "FOTO_CENSO"
    private String fotoUrl;
    private LocalDateTime fechaCarga;
}
