package co.rufe.rufe.service;

import co.rufe.rufe.dao.IEventoRealDao;
import co.rufe.rufe.dao.IPlanificacionEntregaDao;
import co.rufe.rufe.exception.AuthorizationException;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.model.PlanificacionEntrega;
import co.rufe.rufe.dto.PlanificacionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanificacionEntregaService {

    private final IPlanificacionEntregaDao planificacionDao;
    private final IEventoRealDao eventoRealDao;

    @Transactional
    public PlanificacionEntrega planificar(Long organizacionId, PlanificacionRequest request) {
        // Validar si ya existe planificación PENDIENTE para evitar duplicados
        if (planificacionDao.existsByRegistroRufeIdAndAyudaCatalogoIdAndEstado(
                request.getRegistroRufeId(), request.getAyudaCatalogoId(), "PENDIENTE")) {
            throw new RuntimeException("Ya existe una planificación pendiente para este artículo en este registro.");
        }

        // Validar que el evento pertenezca a la organización
        if (request.getEventoId() != null) {
            eventoRealDao.findByIdAndOrganizacionId(request.getEventoId(), organizacionId)
                    .orElseThrow(() -> new AuthorizationException(
                            "El evento no pertenece a la organización o no existe."));
        }

        PlanificacionEntrega plan = PlanificacionEntrega.builder()
                .organizacionId(organizacionId)
                .eventoId(request.getEventoId())
                .registroRufeId(request.getRegistroRufeId())
                .ayudaCatalogoId(request.getAyudaCatalogoId())
                .cantidad(request.getCantidad())
                .estado("PENDIENTE")
                .build();

        return planificacionDao.save(plan);
    }

    /**
     * Obtiene planificaciones de un evento.
     * ADMIN_GLOBAL puede ver planificaciones de cualquier evento.
     * Usuarios normales solo pueden ver planificaciones de eventos de su organización.
     */
    public List<PlanificacionEntrega> obtenerPlanificacionEvento(Long eventoId, Long organizacionId, boolean isAdmin) {
        if (!isAdmin) {
            // Verificar que el evento pertenezca a la organización del usuario
            eventoRealDao.findByIdAndOrganizacionId(eventoId, organizacionId)
                    .orElseThrow(() -> new AuthorizationException(
                            "No tiene acceso a las planificaciones de este evento."));
        }
        return planificacionDao.findAllByEventoId(eventoId);
    }

    public List<PlanificacionEntrega> obtenerPendientes(Long organizacionId) {
        return planificacionDao.findAllByOrganizacionIdAndEstado(organizacionId, "PENDIENTE");
    }

    /**
     * Elimina una planificación verificando que pertenezca a la organización del usuario.
     * ADMIN_GLOBAL puede eliminar planificaciones de cualquier organización.
     */
    @Transactional
    public void eliminarPlanificacion(Long id, Long organizacionId, boolean isAdmin) {
        PlanificacionEntrega plan = planificacionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Planificación no encontrada con ID: " + id));

        if (!isAdmin && !plan.getOrganizacionId().equals(organizacionId)) {
            log.warn("Intento de eliminación no autorizado: planificación ID {} pertenece a org {}, usuario en org {}",
                    id, plan.getOrganizacionId(), organizacionId);
            throw new AuthorizationException(
                    "No tiene permisos para eliminar esta planificación.");
        }

        planificacionDao.deleteById(id);
        log.info("Planificación ID {} eliminada por usuario de org {} (isAdmin={})", id, organizacionId, isAdmin);
    }
}
