package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.dao.IOrganizacionDao; // Necesitamos el DAO de organización para validar
import co.rufe.rufe.dto.rol.RolRequest;
import co.rufe.rufe.dto.rol.RolResponse;
import co.rufe.rufe.exception.AuthorizationException;
import co.rufe.rufe.exception.DuplicateResourceException;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.mapper.RolMapper;
import co.rufe.rufe.model.Rol;
import co.rufe.rufe.service.IRolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RolServiceImpl implements IRolService {

    private final IRolDao rolDao;
    private final IOrganizacionDao organizacionDao;

    public RolServiceImpl(IRolDao rolDao, IOrganizacionDao organizacionDao) {
        this.rolDao = rolDao;
        this.organizacionDao = organizacionDao;
    }

    @Override
    @Transactional
    public RolResponse createRol(Long organizacionId, RolRequest request) {
        log.info("Creando rol '{}' para organización ID: {}", request.getNombreRol(), organizacionId);

        // 1. Validar que la organización exista
        if (!organizacionDao.existsById(organizacionId)) {
            throw new ResourceNotFoundException("Organización no encontrada con ID: " + organizacionId);
        }

        // 2. Validar que el nombre del rol no exista dentro de la misma organización
        if (rolDao.existsByOrganizacionIdAndNombreRol(organizacionId, request.getNombreRol())) {
            throw new DuplicateResourceException("Ya existe un rol con el nombre '" + request.getNombreRol() + "' en la organización con ID: " + organizacionId);
        }

        Rol rol = RolMapper.toModel(request);
        rol.setOrganizacionId(organizacionId); // Asignar el ID de la organización

        try {
            Rol savedRol = rolDao.save(rol);
            log.info("Rol con ID {} creado para la organización ID {}.", savedRol.getId(), organizacionId);
            return RolMapper.toResponse(savedRol);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al crear rol para org ID {}: {}", organizacionId, e.getMessage(), e);
            throw new IllegalArgumentException("Error al crear el rol. Verifique los datos e intente de nuevo.");
        }
    }

    @Override
    public RolResponse getRolById(Long rolId) {
        log.debug("Buscando rol con ID: {}", rolId);
        Rol rol = rolDao.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));
        return RolMapper.toResponse(rol);
    }

    @Override
    public RolResponse getRolByNombre(Long organizacionId, String nombreRol) {
        log.debug("Buscando rol '{}' para organización ID: {}", nombreRol, organizacionId);
        Rol rol = rolDao.findByOrganizacionIdAndNombreRol(organizacionId, nombreRol)
                .orElseThrow(() -> new ResourceNotFoundException("Rol '" + nombreRol + "' no encontrado para la organización ID: " + organizacionId));
        return RolMapper.toResponse(rol);
    }

    @Override
    public List<RolResponse> getRolesByOrganizacionId(Long organizacionId) {
        log.debug("Obteniendo roles para organización ID: {}", organizacionId);
        if (!organizacionDao.existsById(organizacionId)) {
            throw new ResourceNotFoundException("Organización no encontrada con ID: " + organizacionId);
        }
        return rolDao.findByOrganizacionId(organizacionId).stream()
                .map(RolMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RolResponse updateRol(Long rolId, Long organizacionId, RolRequest request) {
        log.info("Actualizando rol con ID {} para organización ID {}.", rolId, organizacionId);

        Rol existingRol = rolDao.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        // Asegurarse de que el rol pertenece a la organización correcta
        if (!existingRol.getOrganizacionId().equals(organizacionId)) {
            throw new AuthorizationException("El rol con ID " + rolId + " no pertenece a la organización ID " + organizacionId + ".");
        }

        // Verificar si el nuevo nombre de rol ya existe para esta organización (y no es el mismo rol)
        if (!existingRol.getNombreRol().equals(request.getNombreRol()) &&
                rolDao.existsByOrganizacionIdAndNombreRol(organizacionId, request.getNombreRol())) {
            throw new DuplicateResourceException("Ya existe otro rol con el nombre '" + request.getNombreRol() + "' en la organización con ID: " + organizacionId);
        }

        existingRol.setNombreRol(request.getNombreRol());
        existingRol.setDescripcion(request.getDescripcion());

        try {
            Rol updatedRol = rolDao.update(existingRol);
            log.info("Rol con ID {} actualizado exitosamente para la organización ID {}.", rolId, organizacionId);
            return RolMapper.toResponse(updatedRol);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al actualizar rol con ID {}: {}", rolId, e.getMessage(), e);
            throw new IllegalArgumentException("Error al actualizar el rol. Verifique los datos e intente de nuevo.");
        }
    }

    @Override
    @Transactional
    public void deleteRol(Long rolId, Long organizacionId) {
        log.info("Intentando eliminar rol con ID {} para organización ID {}.", rolId, organizacionId);

        Rol existingRol = rolDao.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        // Asegurarse de que el rol pertenece a la organización correcta
        if (!existingRol.getOrganizacionId().equals(organizacionId)) {
            throw new AuthorizationException("El rol con ID " + rolId + " no pertenece a la organización ID " + organizacionId + ".");
        }

        try {
            boolean deleted = rolDao.deleteById(rolId);
            if (!deleted) {
                throw new ResourceNotFoundException("Fallo al eliminar el rol con ID: " + rolId);
            }
            log.info("Rol con ID {} eliminado exitosamente para organización ID {}.", rolId, organizacionId);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al eliminar rol con ID {}: {}", rolId, e.getMessage(), e);
            throw new IllegalArgumentException("No se puede eliminar el rol con ID " + rolId + " debido a usuarios u otros registros asociados. Elimine los registros dependientes primero.");
        }
    }
}
