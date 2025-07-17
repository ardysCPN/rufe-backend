package co.rufe.rufe.service;

import co.rufe.rufe.dto.usuario.UsuarioRequest;
import co.rufe.rufe.dto.usuario.UsuarioResponse;

import java.util.List;

public interface IUsuarioService {
    UsuarioResponse createUsuario(Long organizacionId, UsuarioRequest request);
    UsuarioResponse getUsuarioById(Long id);
    UsuarioResponse getUsuarioByEmailAndOrganizacionId(Long organizacionId, String email);
    List<UsuarioResponse> getUsuariosByOrganizacionId(Long organizacionId);
    UsuarioResponse updateUsuario(Long id, Long organizacionId, UsuarioRequest request);
    void deleteUsuario(Long id, Long organizacionId);
    UsuarioResponse getUsuarioByEmail(Long organizacionId, String email);
}
