package co.rufe.rufe.mapper;

import co.rufe.rufe.dto.rol.RolRequest;
import co.rufe.rufe.dto.rol.RolResponse;
import co.rufe.rufe.model.Rol;

public class RolMapper {

    public static Rol toModel(RolRequest request) {
        if (request == null) {
            return null;
        }
        Rol rol = new Rol();
        rol.setNombreRol(request.getNombreRol());
        rol.setDescripcion(request.getDescripcion());
        // organizacionId será establecido en la capa de servicio o DAO
        return rol;
    }

    public static RolResponse toResponse(Rol rol) {
        if (rol == null) {
            return null;
        }
        RolResponse response = new RolResponse();
        response.setId(rol.getId());
        response.setOrganizacionId(rol.getOrganizacionId());
        response.setNombreRol(rol.getNombreRol());
        response.setDescripcion(rol.getDescripcion());
        response.setFechaCreacion(rol.getFechaCreacion());
        response.setFechaActualizacion(rol.getFechaActualizacion());
        return response;
    }
}
