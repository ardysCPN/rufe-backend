package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AyudasEntregadas {
    private Long id;
    private Long organizacionId;
    private Long registroRufeId;
    private Integer ayudaCatalogoId;
    private BigDecimal cantidad;
    private String firmaDigital; // base64 or reference
    private String evidenciaFotoUrl; // URL from S3
    private LocalDateTime fechaEntrega;
    private AyudaCatalogo ayudaCatalogo;
}
