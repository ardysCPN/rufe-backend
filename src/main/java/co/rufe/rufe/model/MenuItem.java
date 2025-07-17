package co.rufe.rufe.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class MenuItem {
    private Long id;
    private Long parentId; // Puede ser null
    private String nombreItem;
    private String ruta;
    private String icono;
    private Integer orden;
}
