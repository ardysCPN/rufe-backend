package co.rufe.rufe.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
@Builder
public class Rol {
    private Long id;
    private Long organizacionId;
    private String nombreRol;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
