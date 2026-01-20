package co.rufe.rufe.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {

    @Schema(description = "ID único del ítem de menú", example = "1")
    private Integer id; // Asegúrate de que es Integer

    @Schema(description = "ID del ítem de menú padre (null para ítems raíz)", example = "null")
    private Integer parentId; // Asegúrate de que es Integer

    @Schema(description = "Nombre visible del ítem de menú", example = "Gestión de Usuarios")
    private String nombreItem;

    @Schema(description = "Ruta URL asociada al ítem de menú", example = "/admin/users")
    private String ruta;

    @Schema(description = "Nombre del icono para el ítem de menú (ej. de Material Icons)", example = "group")
    private String icono;

    @Schema(description = "Orden de aparición del ítem de menú en su nivel", example = "10")
    private Integer orden;

    @Schema(description = "Sub-ítems de menú", example = "[]")
    private List<MenuItemResponse> subItems; // Para la estructura de árbol

    // Nota: Los permisos requeridos para mostrar este MenuItem no se incluyen aquí
    // ya que la lista de permisos se usa *para filtrar* el menú antes de enviarlo al frontend.
    // El frontend solo recibe el menú que *ya puede ver*.
}