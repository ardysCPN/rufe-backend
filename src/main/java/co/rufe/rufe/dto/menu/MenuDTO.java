package co.rufe.rufe.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    private Long id;
    private String nombre;
    private String ruta;
    private String icono;
    private Integer orden;
    private Boolean offlineCompatible;
    private List<MenuDTO> children;
}
