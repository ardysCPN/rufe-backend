package co.rufe.rufe.dto.permiso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoResponse {
    private Long id;
    private String nombrePermiso;
    private String descripcion;
    private String recurso;
}
