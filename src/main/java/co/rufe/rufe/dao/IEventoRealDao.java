package co.rufe.rufe.dao;

import co.rufe.rufe.model.EventoReal;
import java.util.List;
import java.util.Optional;

public interface IEventoRealDao {
    EventoReal save(EventoReal evento);

    void update(EventoReal evento);

    Optional<EventoReal> findById(Long id);

    Optional<EventoReal> findByIdAndOrganizacionId(Long id, Long organizacionId);

    List<EventoReal> findAll();

    List<EventoReal> findAllByOrganizacionId(Long organizacionId);

    void deleteLogical(Long id, Long organizacionId);

    boolean existsByClienteIdAndOrganizacionId(String clienteId, Long organizacionId);
}
