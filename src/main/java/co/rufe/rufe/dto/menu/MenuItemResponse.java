package co.rufe.rufe.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO para la respuesta de un ítem de menú")
public class MenuItemResponse {

    @Schema(description = "ID único del ítem de menú", example = "1")
    private Long id;

    @Schema(description = "ID del ítem de menú padre (nulo si es de nivel superior)", example = "null")
    private Long parentId;

    @Schema(description = "Nombre del ítem de menú", example = "Gestión de Usuarios")
    private String nombreItem;

    @Schema(description = "Ruta de la interfaz de usuario asociada", example = "/dashboard/users")
    private String ruta;

    @Schema(description = "Clase del icono", example = "fas fa-users")
    private String icono;

    @Schema(description = "Orden de visualización", example = "1")
    private Integer orden;

    @Schema(description = "Lista de sub-ítems de menú", implementation = MenuItemResponse.class)
    private List<MenuItemResponse> subItems; // Para representar la estructura jerárquica
}
