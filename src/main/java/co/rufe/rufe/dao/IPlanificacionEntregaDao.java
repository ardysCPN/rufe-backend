package co.rufe.rufe.dao;

import co.rufe.rufe.model.PlanificacionEntrega;
import java.util.List;
import java.util.Optional;

public interface IPlanificacionEntregaDao {
    PlanificacionEntrega save(PlanificacionEntrega plan);
    Optional<PlanificacionEntrega> findById(Long id);
    List<PlanificacionEntrega> findAllByEventoId(Long eventoId);
    List<PlanificacionEntrega> findAllByOrganizacionIdAndEstado(Long organizacionId, String estado);
    void deleteById(Long id);
    void updateEstado(Long id, String estado);
    
    // Validar si ya existe planificación para este RUFE y Artículo en este Evento
    boolean existsByRegistroRufeIdAndAyudaCatalogoIdAndEstado(Long rufeId, Integer ayudaId, String estado);
}
