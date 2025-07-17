package co.rufe.rufe.mapper;


import co.rufe.rufe.dto.usuario.UsuarioRequest;
import co.rufe.rufe.dto.usuario.UsuarioResponse;
import co.rufe.rufe.model.Usuario;

public class UsuarioMapper {

    public static Usuario toModel(UsuarioRequest request) {
        if (request == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(request.getPassword()); // La contraseña se hasheará en el servicio
        usuario.setRolId(request.getRolId());
        usuario.setActivo(request.getActivo() != null ? request.getActivo() : true);
        // organizacionId será establecido en la capa de servicio o DAO
        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setOrganizacionId(usuario.getOrganizacionId());
        response.setRolId(usuario.getRolId());
        response.setNombreCompleto(usuario.getNombreCompleto());
        response.setEmail(usuario.getEmail());
        response.setActivo(usuario.getActivo());
        response.setFechaCreacion(usuario.getFechaCreacion());
        response.setFechaActualizacion(usuario.getFechaActualizacion());
        return response;
    }
}
