package co.rufe.rufe.service;

import co.rufe.rufe.dto.organizacion.OrganizacionRequest;
import co.rufe.rufe.dto.organizacion.OrganizacionResponse;

import java.util.List;

public interface IOrganizacionService {
    OrganizacionResponse createOrganizacion(OrganizacionRequest request);
    OrganizacionResponse getOrganizacionById(Long id);
    OrganizacionResponse getOrganizacionByNombre(String nombreOrganizacion);
    List<OrganizacionResponse> getAllOrganizaciones();
    OrganizacionResponse updateOrganizacion(Long id, OrganizacionRequest request);
    void deleteOrganizacion(Long id);
}
