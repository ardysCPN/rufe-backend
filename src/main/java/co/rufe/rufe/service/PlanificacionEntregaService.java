package co.rufe.rufe.service;

import co.rufe.rufe.dao.IPlanificacionEntregaDao;
import co.rufe.rufe.model.PlanificacionEntrega;
import co.rufe.rufe.dto.PlanificacionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanificacionEntregaService {

    private final IPlanificacionEntregaDao planificacionDao;

    @Transactional
    public PlanificacionEntrega planificar(Long organizacionId, PlanificacionRequest request) {
        // Validar si ya existe planificación PENDIENTE para evitar duplicados
        if (planificacionDao.existsByRegistroRufeIdAndAyudaCatalogoIdAndEstado(
                request.getRegistroRufeId(), request.getAyudaCatalogoId(), "PENDIENTE")) {
            throw new RuntimeException("Ya existe una planificación pendiente para este artículo en este registro.");
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

    public List<PlanificacionEntrega> obtenerPlanificacionEvento(Long eventoId) {
        return planificacionDao.findAllByEventoId(eventoId);
    }

    public List<PlanificacionEntrega> obtenerPendientes(Long organizacionId) {
        return planificacionDao.findAllByOrganizacionIdAndEstado(organizacionId, "PENDIENTE");
    }

    @Transactional
    public void eliminarPlanificacion(Long id) {
        planificacionDao.deleteById(id);
    }
}
