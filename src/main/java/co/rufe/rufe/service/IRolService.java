package co.rufe.rufe.service;

import co.rufe.rufe.dto.rol.RolRequest;
import co.rufe.rufe.dto.rol.RolResponse;

import java.util.List;

public interface IRolService {
    RolResponse createRol(Long organizacionId, RolRequest request);
    RolResponse getRolById(Long rolId);
    RolResponse getRolByNombre(Long organizacionId, String nombreRol);
    List<RolResponse> getRolesByOrganizacionId(Long organizacionId);
    RolResponse updateRol(Long rolId, Long organizacionId, RolRequest request);
    void deleteRol(Long rolId, Long organizacionId);
}
