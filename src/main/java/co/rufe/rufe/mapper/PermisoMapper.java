package co.rufe.rufe.mapper;

import co.rufe.rufe.dto.permiso.PermisoResponse;
import co.rufe.rufe.model.Permiso;

public class PermisoMapper {

    // Convierte entidad a DTO de respuesta
    public static PermisoResponse toResponse(Permiso permiso) {
        if (permiso == null) {
            return null;
        }
        return PermisoResponse.builder()
                .id(permiso.getId())
                .nombrePermiso(permiso.getNombrePermiso())
                .descripcion(permiso.getDescripcion())
                .recurso(permiso.getRecurso())
                .build();
    }

    // Si necesitaras un toModel (para crear/actualizar permisos desde un DTO de Request)
    // public static Permiso toModel(PermisoRequest request) { ... }
}