package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IRegistroRufeDao;
import co.rufe.rufe.dto.rufe.*;
import co.rufe.rufe.exception.DuplicateResourceException;
import co.rufe.rufe.exception.BusinessRuleException;
import co.rufe.rufe.model.*;
import co.rufe.rufe.service.IRegistroRufeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private void validateIntegrantes(List<IntegranteHogarRequest> integrantes, Long eventoId) {
        if (integrantes == null || integrantes.isEmpty()) {
            throw new IllegalArgumentException("El registro debe tener al menos un integrante.");
        }

        Set<String> documentosEnRequest = new HashSet<>();
        for (IntegranteHogarRequest i : integrantes) {
            if (i.getNumeroDocumento() != null && !documentosEnRequest.add(i.getNumeroDocumento())) {
                throw new BusinessRuleException(
                        "El documento " + i.getNumeroDocumento() + " está duplicado internamente.");
            }
            if (i.getNumeroDocumento() != null
                    && registroRufeDao.existsByNumeroDocumentoAndEventoId(i.getNumeroDocumento(), eventoId)) {
                throw new BusinessRuleException(
                        "El integrante con documento " + i.getNumeroDocumento() + " ya existe en este evento.");
            }
        }
    }
}
