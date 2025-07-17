package co.rufe.rufe.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para la creación/actualización de un ítem de menú")
public class MenuItemRequest {

    @Schema(description = "ID del ítem de menú padre (opcional)", example = "1")
    private Long parentId;

    @NotBlank(message = "El nombre del ítem de menú no puede estar vacío.")
    @Size(min = 2, max = 100, message = "El nombre del ítem debe tener entre 2 y 100 caracteres.")
    @Schema(description = "Nombre del ítem de menú", example = "Gestión de Usuarios")
    private String nombreItem;

    @Schema(description = "Ruta de la interfaz de usuario asociada al ítem de menú", example = "/dashboard/users")
    private String ruta;

    @Schema(description = "Clase del icono para el ítem de menú", example = "fas fa-users")
    private String icono;

    @NotNull(message = "El orden del ítem de menú no puede ser nulo.")
    @Schema(description = "Orden de visualización del ítem de menú", example = "1")
    private Integer orden;
}
