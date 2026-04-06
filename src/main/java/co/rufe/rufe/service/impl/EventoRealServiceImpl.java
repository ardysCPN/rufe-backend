package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IEventoRealDao;
import co.rufe.rufe.dto.evento.EventoRealRequest;
import co.rufe.rufe.dto.evento.EventoRealResponse;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.model.EventoReal;
import co.rufe.rufe.service.IEventoRealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventoRealServiceImpl implements IEventoRealService {

    private final IEventoRealDao eventoDao;

    public EventoRealServiceImpl(IEventoRealDao eventoDao) {
        this.eventoDao = eventoDao;
    }

    @Override
    @Transactional
    public EventoRealResponse createEvento(EventoRealRequest request, Long organizacionId) {
        String clienteId = request.getClienteId();
        if (clienteId == null || clienteId.isEmpty()) {
            clienteId = UUID.randomUUID().toString();
        }

        EventoReal evento = EventoReal.builder()
                .organizacionId(organizacionId)
                .clienteId(clienteId)
                .nombreEvento(request.getNombreEvento())
                .tipoEvento(request.getTipoEvento())
                .fechaEvento(request.getFechaEvento())
                .departamento(request.getDepartamento())
                .municipio(request.getMunicipio())
                .descripcion(request.getDescripcion())
                .estado(request.getEstado() != null ? request.getEstado() : "ABIERTO")
                .build();

        EventoReal saved = eventoDao.save(evento);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public EventoRealResponse updateEvento(Long id, EventoRealRequest request, Long organizacionId) {
        EventoReal existing = (organizacionId == null)
                ? eventoDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado."))
                : eventoDao.findByIdAndOrganizacionId(id, organizacionId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Evento no encontrado o no pertenece a la organización."));

        existing.setNombreEvento(request.getNombreEvento());
        existing.setTipoEvento(request.getTipoEvento());
        existing.setFechaEvento(request.getFechaEvento());
        existing.setDepartamento(request.getDepartamento());
        existing.setMunicipio(request.getMunicipio());
        existing.setDescripcion(request.getDescripcion());
        existing.setEstado(request.getEstado());

        eventoDao.update(existing);
        return toResponse(existing);
    }

    @Override
    public EventoRealResponse getEventoById(Long id, Long organizacionId, boolean isAdmin) {
        EventoReal evento = isAdmin
                ? eventoDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado."))
                : eventoDao.findByIdAndOrganizacionId(id, organizacionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado."));
        return toResponse(evento);
    }

    @Override
    public List<EventoRealResponse> getAllEventos(Long organizacionId, boolean isAdmin) {
        List<EventoReal> list = isAdmin ? eventoDao.findAll() : eventoDao.findAllByOrganizacionId(organizacionId);
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEvento(Long id, Long organizacionId) {
        if (eventoDao.findByIdAndOrganizacionId(id, organizacionId).isEmpty()) {
            throw new ResourceNotFoundException("Evento no encontrado para eliminar.");
        }
        eventoDao.deleteLogical(id, organizacionId);
    }

    private EventoRealResponse toResponse(EventoReal evento) {
        return EventoRealResponse.builder()
                .id(evento.getId())
                .clienteId(evento.getClienteId())
                .nombreEvento(evento.getNombreEvento())
                .tipoEvento(evento.getTipoEvento())
                .fechaEvento(evento.getFechaEvento())
                .departamento(evento.getDepartamento())
                .municipio(evento.getMunicipio())
                .descripcion(evento.getDescripcion())
                .estado(evento.getEstado())
                .fechaCreacion(evento.getFechaCreacion())
                .fechaActualizacion(evento.getFechaActualizacion())
                .build();
    }
}
