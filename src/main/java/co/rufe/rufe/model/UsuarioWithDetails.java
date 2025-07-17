package co.rufe.rufe.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioWithDetails {
    private Long id;
    private Long organizacionId;
    private String organizacionNombre;
    private Boolean organizacionActiva;
    private Long rolId;
    private String rolNombre;
    private String nombreCompleto;
    private String email;
    private String passwordHash;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
