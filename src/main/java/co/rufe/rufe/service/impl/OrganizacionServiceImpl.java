package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IOrganizacionDao;
import co.rufe.rufe.dto.organizacion.OrganizacionRequest;
import co.rufe.rufe.dto.organizacion.OrganizacionResponse;
import co.rufe.rufe.exception.DuplicateResourceException;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.mapper.OrganizacionMapper;
import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.service.IOrganizacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrganizacionServiceImpl implements IOrganizacionService {

    private final IOrganizacionDao organizacionDao;

    public OrganizacionServiceImpl(IOrganizacionDao organizacionDao) {
        this.organizacionDao = organizacionDao;
    }

    @Override
    @Transactional
    public OrganizacionResponse createOrganizacion(OrganizacionRequest request) {
        log.info("Creando organización con nombre: {}", request.getNombreOrganizacion());
        if (organizacionDao.existsByNombreOrganizacion(request.getNombreOrganizacion())) {
            throw new DuplicateResourceException("Ya existe una organización con el nombre '" + request.getNombreOrganizacion() + "'.");
        }
        Organizacion organizacion = OrganizacionMapper.toModel(request);
        try {
            Organizacion savedOrganizacion = organizacionDao.save(organizacion);
            log.info("Organización creada con ID: {}", savedOrganizacion.getId());
            return OrganizacionMapper.toResponse(savedOrganizacion);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al crear organización: {}", e.getMessage(), e);
            throw new DuplicateResourceException("Error al crear organización. Puede que el nombre ya exista. " + e.getMessage());
        }
    }

    @Override
    public OrganizacionResponse getOrganizacionById(Long id) {
        log.debug("Buscando organización con ID: {}", id);
        Organizacion organizacion = organizacionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada con ID: " + id));
        return OrganizacionMapper.toResponse(organizacion);
    }

    @Override
    public OrganizacionResponse getOrganizacionByNombre(String nombreOrganizacion) {
        log.debug("Buscando organización con nombre: {}", nombreOrganizacion);
        Organizacion organizacion = organizacionDao.findByNombreOrganizacion(nombreOrganizacion)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada con nombre: " + nombreOrganizacion));
        return OrganizacionMapper.toResponse(organizacion);
    }

    @Override
    public List<OrganizacionResponse> getAllOrganizaciones() {
        log.debug("Obteniendo todas las organizaciones.");
        return organizacionDao.findAll().stream()
                .map(OrganizacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganizacionResponse updateOrganizacion(Long id, OrganizacionRequest request) {
        log.info("Actualizando organización con ID: {}", id);
        Organizacion existingOrganizacion = organizacionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada con ID: " + id));

        // Verificar si el nuevo nombre de organización ya existe y es diferente al actual
        if (!existingOrganizacion.getNombreOrganizacion().equals(request.getNombreOrganizacion()) &&
                organizacionDao.existsByNombreOrganizacion(request.getNombreOrganizacion())) {
            throw new DuplicateResourceException("Ya existe otra organización con el nombre '" + request.getNombreOrganizacion() + "'.");
        }

        existingOrganizacion.setNombreOrganizacion(request.getNombreOrganizacion());
        // Solo actualizar 'activa' si el request lo provee. Si es null, mantiene el valor existente.
        if (request.getActiva() != null) {
            existingOrganizacion.setActiva(request.getActiva());
        }

        try {
            Organizacion updatedOrganizacion = organizacionDao.update(existingOrganizacion);
            log.info("Organización con ID {} actualizada exitosamente.", updatedOrganizacion.getId());
            return OrganizacionMapper.toResponse(updatedOrganizacion);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al actualizar organización con ID {}: {}", id, e.getMessage(), e);
            throw new DuplicateResourceException("Error al actualizar organización. Puede que el nombre ya exista. " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteOrganizacion(Long id) {
        log.info("Intentando eliminar organización con ID: {}", id);
        if (!organizacionDao.existsById(id)) {
            throw new ResourceNotFoundException("Organización no encontrada con ID: " + id);
        }
        try {
            boolean deleted = organizacionDao.deleteById(id);
            if (!deleted) {
                // Esto debería ser capturado por ResourceNotFoundException, pero es un fallback.
                throw new IllegalStateException("Fallo al eliminar la organización con ID: " + id);
            }
            log.info("Organización con ID {} eliminada exitosamente.", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al eliminar organización con ID {}: {}", id, e.getMessage(), e);
            throw new IllegalArgumentException("No se puede eliminar la organización con ID " + id + " debido a registros asociados (roles, usuarios, eventos, etc.). Elimine los registros dependientes primero.");
        }
    }
}
