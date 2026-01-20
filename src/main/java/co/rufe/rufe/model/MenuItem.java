package co.rufe.rufe.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
@Builder
public class MenuItem {
    private Integer id; 
    private Integer parentId; 
    private String nombreItem;
    private String ruta;
    private String icono;
    private Integer orden;
}
