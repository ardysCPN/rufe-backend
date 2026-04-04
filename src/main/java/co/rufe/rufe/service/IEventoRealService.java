package co.rufe.rufe.service;

import co.rufe.rufe.dto.evento.EventoRealRequest;
import co.rufe.rufe.dto.evento.EventoRealResponse;
import java.util.List;

public interface IEventoRealService {
    EventoRealResponse createEvento(EventoRealRequest request, Long organizacionId);

    EventoRealResponse updateEvento(Long id, EventoRealRequest request, Long organizacionId);

    EventoRealResponse getEventoById(Long id, Long organizacionId, boolean isAdmin);

    List<EventoRealResponse> getAllEventos(Long organizacionId, boolean isAdmin);

    void deleteEvento(Long id, Long organizacionId);
}
