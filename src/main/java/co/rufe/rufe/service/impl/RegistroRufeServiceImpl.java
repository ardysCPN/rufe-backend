package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IRegistroRufeDao;
import co.rufe.rufe.dto.rufe.*;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.exception.DuplicateResourceException;
import co.rufe.rufe.exception.BusinessRuleException;
import co.rufe.rufe.model.*;
import co.rufe.rufe.service.IRegistroRufeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegistroRufeServiceImpl implements IRegistroRufeService {

    private final IRegistroRufeDao registroRufeDao;

    public RegistroRufeServiceImpl(IRegistroRufeDao registroRufeDao) {
        this.registroRufeDao = registroRufeDao;
    }

    @Override
    @Transactional
    public RegistroRufeResponse crearRegistro(RegistroRufeCreateRequest request, Long usuarioRegistradorId,
            Long organizacionId) {
        log.info("Procesando registro RUFE. ClienteId: {}, OrganizacionId: {}", request.getClienteId(), organizacionId);

        if (registroRufeDao.existsByClienteIdAndOrganizacionId(request.getClienteId(), organizacionId)) {
            log.warn("Registro duplicado detectado para ClienteId: {}", request.getClienteId());
            throw new DuplicateResourceException(
                    "El registro con Cliente ID " + request.getClienteId() + " ya fue procesado.");
        }

        validateIntegrantes(request.getIntegrantes(), request.getEventoId());

        RegistroRufe registro = RegistroRufe.builder()
                .organizacionId(organizacionId)
                .eventoId(request.getEventoId())
                .tipoEventoId(request.getTipoEventoId())
                .usuarioRegistradorId(usuarioRegistradorId)
                .clienteId(request.getClienteId())
                .fechaRegistro(request.getFechaRegistro())
                .tipoUbicacionBienId(request.getTipoUbicacionBienId())
                .corregimiento(request.getCorregimiento())
                .veredaSectorBarrio(request.getVeredaSectorBarrio())
                .direccion(request.getDireccion())
                .tipoAlojamientoActualId(request.getTipoAlojamientoActualId())
                .lugarHabitualResidencia(request.getLugarHabitualResidencia())
                .evacuadoFueraResidencia(request.getEvacuadoFueraResidencia())
                .observaciones(request.getObservaciones())
                .voBoCmgrd(request.getVoBoCmgrd())
                .build();

        RegistroRufe savedRegistro = registroRufeDao.save(registro);

        if (request.getIntegrantes() != null) {
            for (IntegranteHogarRequest integranteReq : request.getIntegrantes()) {
                IntegranteHogar integrante = IntegranteHogar.builder()
                        .registroRufeId(savedRegistro.getId())
                        .clienteId(integranteReq.getClienteId())
                        .registroRufeClienteId(request.getClienteId())
                        .nombres(integranteReq.getNombres())
                        .apellidos(integranteReq.getApellidos())
                        .tipoDocumentoId(integranteReq.getTipoDocumentoId())
                        .numeroDocumento(integranteReq.getNumeroDocumento())
                        .fechaNacimiento(integranteReq.getFechaNacimiento())
                        .parentescoId(integranteReq.getParentescoId())
                        .generoId(integranteReq.getGeneroId())
                        .pertenenciaEtnicaId(integranteReq.getPertenenciaEtnicaId())
                        .telefono(integranteReq.getTelefono())
                        .build();

                registroRufeDao.saveIntegrante(integrante);
            }
        }

        if (request.getBienesAfectados() != null) {
            for (BienAfectadoRequest bienReq : request.getBienesAfectados()) {
                BienAfectado bien = BienAfectado.builder()
                        .registroRufeId(savedRegistro.getId())
                        .clienteId(bienReq.getClienteId())
                        .registroRufeClienteId(request.getClienteId())
                        .tipoBienId(bienReq.getTipoBienId())
                        .formaTenenciaBienId(bienReq.getFormaTenenciaBienId())
                        .estadoBienId(bienReq.getEstadoBienId())
                        .build();
                registroRufeDao.saveBienAfectado(bien);
            }
        }

        if (request.getActivosAgropecuarios() != null) {
            for (ActivoAgropecuarioRequest activoReq : request.getActivosAgropecuarios()) {
                ActivoAgropecuario activo = ActivoAgropecuario.builder()
                        .registroRufeId(savedRegistro.getId())
                        .clienteId(activoReq.getClienteId())
                        .registroRufeClienteId(request.getClienteId())
                        .sector(activoReq.getSector())
                        .tipoCultivo(activoReq.getTipoCultivo())
                        .unidadMedidaAgricola(activoReq.getUnidadMedidaAgricola())
                        .areaCantidadAgricola(activoReq.getAreaCantidadAgricola())
                        .especieAnimal(activoReq.getEspecieAnimal())
                        .cantidadAnimal(activoReq.getCantidadAnimal())
                        .build();
                registroRufeDao.saveActivoAgropecuario(activo);
            }
        }

        return RegistroRufeResponse.builder()
                .id(savedRegistro.getId())
                .clienteId(savedRegistro.getClienteId())
                .estado("GUARDADO")
                .mensaje("Registro procesado exitosamente.")
                .fechaRecepcion(LocalDateTime.now())
                .build();
    }

    @Override
    public List<RegistroRufeResponse> listarTodos(Long organizacionId, boolean isAdmin) {
        log.info("Listando registros RUFE. OrganizacionId: {}, IsAdmin: {}", organizacionId, isAdmin);
        List<RegistroRufe> registros = isAdmin ? registroRufeDao.findAll()
                : registroRufeDao.findAllByOrganizacionId(organizacionId);

        return registros.stream()
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RegistroRufeResponse obtenerPorId(Long id, Long organizacionId, boolean isAdmin) {
        log.info("Consultando registro RUFE ID: {}. OrganizacionId: {}, IsAdmin: {}", id, organizacionId, isAdmin);
        RegistroRufe registro = registroRufeDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro RUFE no encontrado."));

        if (!isAdmin && !registro.getOrganizacionId().equals(organizacionId)) {
            log.warn("Intento de acceso no autorizado al RUFE ID {} por organización {}", id, organizacionId);
            throw new co.rufe.rufe.exception.AuthorizationException("No tiene permisos para ver este registro.");
        }

        return toSimpleResponse(registro);
    }

    private RegistroRufeResponse toSimpleResponse(RegistroRufe registro) {
        return RegistroRufeResponse.builder()
                .id(registro.getId())
                .clienteId(registro.getClienteId())
                .fechaRegistro(registro.getFechaRegistro())
                .eventoId(registro.getEventoId())
                .tipoEventoId(registro.getTipoEventoId())
                .corregimiento(registro.getCorregimiento())
                .veredaSectorBarrio(registro.getVeredaSectorBarrio())
                .direccion(registro.getDireccion())
                .estado("SINCRONIZADO")
                .build();
    }

    private void validateIntegrantes(List<IntegranteHogarRequest> integrantes, Long eventoId) {
        if (integrantes == null || integrantes.isEmpty()) {
            throw new BusinessRuleException("El registro debe tener al menos un integrante.");
        }
        for (IntegranteHogarRequest integrante : integrantes) {
            if (registroRufeDao.existsByNumeroDocumentoAndEventoId(integrante.getNumeroDocumento(), eventoId)) {
                throw new DuplicateResourceException(
                        "El integrante con documento " + integrante.getNumeroDocumento()
                                + " ya está registrado en este evento.");
            }
        }
    }
}
