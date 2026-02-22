package co.rufe.rufe.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    private Long id;
    private Long organizacionId;
    private Long usuarioId;
    private String accion;
    private String recurso;
    private String detalle;
    private String ipAddress;
    private LocalDateTime fechaCreacion;
}
