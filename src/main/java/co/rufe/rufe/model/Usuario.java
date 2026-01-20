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
public class Usuario {
    private Long id;
    private Long organizacionId;
    private Long rolId;
    private String nombreCompleto;
    private String email;
    private String passwordHash; // Almacenará la contraseña hasheada
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
