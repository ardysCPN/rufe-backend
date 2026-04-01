package co.rufe.rufe.service;

import co.rufe.rufe.dto.rufe.RegistroRufeCreateRequest;
import co.rufe.rufe.dto.rufe.RegistroRufeResponse;

public interface IRegistroRufeService {

    /**
     * Procesa la creación de un nuevo registro RUFE.
     * Realiza validaciones de negocio, antifraude y persistencia transaccional.
     * 
     * @param request              Datos del formulario
     * @param usuarioRegistradorId ID del usuario autenticado
     * @param organizacionId       ID de la organización del usuario
     * @return Respuesta con el estado del registro
     */
    RegistroRufeResponse crearRegistro(RegistroRufeCreateRequest request, Long usuarioRegistradorId,
            Long organizacionId);

    java.util.List<RegistroRufeResponse> listarTodos(Long organizacionId, boolean isAdmin);

    RegistroRufeResponse obtenerPorId(Long id, Long organizacionId, boolean isAdmin);
}
