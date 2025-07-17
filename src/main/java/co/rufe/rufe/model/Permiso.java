package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {
    private Long id;
    private String nombrePermiso; // Ej: 'usuarios:crear', 'rufe:leer_todos'
    private String descripcion;
    private String recurso;
}
