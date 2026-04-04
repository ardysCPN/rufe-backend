package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRufe {
    private Long id;
    private Long organizacionId;
    private Long eventoId;
    private Long tipoEventoId; // NUEVO: FK a tabla evento (catálogo de tipos)
    private Long usuarioRegistradorId;
    private String clienteId;
    private LocalDateTime fechaRegistro;

    // Nuevos campos según SQL
    private Integer tipoUbicacionBienId;
    private String corregimiento;
    private String veredaSectorBarrio;
    private String direccion;
    private Integer tipoAlojamientoActualId;
    private String lugarHabitualResidencia;
    private Boolean evacuadoFueraResidencia;
    private String observaciones;
    private String voBoCmgrd;

    private String ubicacion; // WKT o GeoJSON representing Point
    private String ubicacionOffline; // JSON payload for caching offline coordinates

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEliminacion;
}
