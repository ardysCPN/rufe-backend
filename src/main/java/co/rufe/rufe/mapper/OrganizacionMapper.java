package co.rufe.rufe.mapper;

import co.rufe.rufe.dto.organizacion.OrganizacionRequest;
import co.rufe.rufe.dto.organizacion.OrganizacionResponse;
import co.rufe.rufe.model.Organizacion;

public class OrganizacionMapper {

    public static Organizacion toModel(OrganizacionRequest request) {
        if (request == null) {
            return null;
        }
        Organizacion organizacion = new Organizacion();
        organizacion.setNombreOrganizacion(request.getNombreOrganizacion());
        organizacion.setNit(request.getNit());
        organizacion.setDireccion(request.getDireccion());
        organizacion.setTelefono(request.getTelefono());
        // Si 'activa' no se proporciona en el request, se asume 'true' por defecto de
        // la BD.
        // Si se proporciona, se usa el valor del request.
        organizacion.setActiva(request.getActiva() != null ? request.getActiva() : true);
        return organizacion;
    }

    public static OrganizacionResponse toResponse(Organizacion organizacion) {
        if (organizacion == null) {
            return null;
        }
        OrganizacionResponse response = new OrganizacionResponse();
        response.setId(organizacion.getId());
        response.setNombreOrganizacion(organizacion.getNombreOrganizacion());
        response.setNit(organizacion.getNit());
        response.setDireccion(organizacion.getDireccion());
        response.setTelefono(organizacion.getTelefono());
        response.setActiva(organizacion.getActiva());
        response.setFechaCreacion(organizacion.getFechaCreacion());
        response.setFechaActualizacion(organizacion.getFechaActualizacion());
        return response;
    }
}
