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
public class Menu {
    private Long id;
    private Long idMenu; // Parent ID
    private Integer idTipoMenu; // 1: Nivel 1, 2: Nivel 2, etc.
    private String routerUrl;
    private String nombreOpcion;
    private String icono;
    private Integer orden;
    private Boolean offlineCompatible;
    private LocalDateTime fechaCreacion;
}
