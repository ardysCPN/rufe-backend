package co.rufe.rufe.service.impl;

import co.rufe.rufe.dao.IOrganizacionDao;
import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.dto.usuario.UsuarioRequest;
import co.rufe.rufe.dto.usuario.UsuarioResponse;
import co.rufe.rufe.exception.AuthorizationException;
import co.rufe.rufe.exception.DuplicateResourceException;
import co.rufe.rufe.exception.ResourceNotFoundException;
import co.rufe.rufe.mapper.UsuarioMapper;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.service.IUsuarioService;
import co.rufe.rufe.util.PasswordHasher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioDao usuarioDao;
    private final IRolDao rolDao;
    private final IOrganizacionDao organizacionDao;
    private final PasswordHasher passwordHasher;

    public UsuarioServiceImpl(IUsuarioDao usuarioDao, IRolDao rolDao, IOrganizacionDao organizacionDao, PasswordHasher passwordHasher) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
        this.organizacionDao = organizacionDao;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public UsuarioResponse createUsuario(Long organizacionId, UsuarioRequest request) {
        log.info("Creando usuario '{}' para organización ID: {}", request.getEmail(), organizacionId);

        // 1. Validar que la organización exista
        if (!organizacionDao.existsById(organizacionId)) {
            throw new ResourceNotFoundException("Organización no encontrada con ID: " + organizacionId);
        }

        // 2. Validar que el rol exista y pertenezca a la misma organización
        if (!rolDao.existsById(request.getRolId())) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + request.getRolId());
        }
        rolDao.findById(request.getRolId()).ifPresent(rol -> {
            if (!rol.getOrganizacionId().equals(organizacionId)) {
                throw new IllegalArgumentException("El rol con ID " + request.getRolId() + " no pertenece a la organización con ID " + organizacionId + ".");
            }
        });


        // 3. Validar que el email no exista dentro de la misma organización
        if (usuarioDao.existsByOrganizacionIdAndEmail(organizacionId, request.getEmail())) {
            throw new DuplicateResourceException("Ya existe un usuario con el email '" + request.getEmail() + "' en la organización con ID: " + organizacionId);
        }

        Usuario usuario = UsuarioMapper.toModel(request);
        usuario.setOrganizacionId(organizacionId); // Asignar el ID de la organización
        usuario.setPasswordHash(passwordHasher.hashPassword(request.getPassword())); // Hashear la contraseña

        try {
            Usuario savedUsuario = usuarioDao.save(usuario);
            log.info("Usuario con ID {} creado para la organización ID {}.", savedUsuario.getId(), organizacionId);
            return UsuarioMapper.toResponse(savedUsuario);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al crear usuario para org ID {}: {}", organizacionId, e.getMessage(), e);
            throw new IllegalArgumentException("Error al crear el usuario. Verifique los datos (email, rol) e intente de nuevo.");
        }
    }

    @Override
    public UsuarioResponse getUsuarioById(Long id) {
        log.debug("Buscando usuario con ID: {}", id);
        Usuario usuario = usuarioDao.findById(id) // Este findById ya valida el tenant por TenantContext
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return UsuarioMapper.toResponse(usuario);
    }

    @Override
    public UsuarioResponse getUsuarioByEmailAndOrganizacionId(Long organizacionId, String email) {
        log.debug("Buscando usuario con email '{}' en organización ID: {}", email, organizacionId);
        // Validar que la organización exista, aunque el DAO ya lo validará implícitamente
        if (!organizacionDao.existsById(organizacionId)) {
             throw new ResourceNotFoundException("Organización no encontrada con ID: " + organizacionId);
        }
        Usuario usuario = usuarioDao.findByOrganizacionIdAndEmail(organizacionId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email '" + email + "' no encontrado para la organización ID: " + organizacionId));
        return UsuarioMapper.toResponse(usuario);
    }

    @Override
    public List<UsuarioResponse> getUsuariosByOrganizacionId(Long organizacionId) {
        log.debug("Obteniendo usuarios para organización ID: {}", organizacionId);
        if (!organizacionDao.existsById(organizacionId)) {
            throw new ResourceNotFoundException("Organización no encontrada con ID: " + organizacionId);
        }
        return usuarioDao.findByOrganizacionId(organizacionId).stream()
                .map(UsuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponse updateUsuario(Long id, Long organizacionId, UsuarioRequest request) {
        log.info("Actualizando usuario con ID {} para organización ID {}.", id, organizacionId);

        Usuario existingUsuario = usuarioDao.findById(id) // Valida que el usuario exista y pertenezca al tenant actual
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Asegurarse de que el usuario pertenece a la organización correcta
        if (!existingUsuario.getOrganizacionId().equals(organizacionId)) {
            throw new AuthorizationException("El usuario con ID " + id + " no pertenece a la organización ID " + organizacionId + ".");
        }

        // Validar que el nuevo rol exista y pertenezca a la misma organización
        if (!rolDao.existsById(request.getRolId())) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + request.getRolId());
        }
        rolDao.findById(request.getRolId()).ifPresent(rol -> {
            if (!rol.getOrganizacionId().equals(organizacionId)) {
                throw new IllegalArgumentException("El rol con ID " + request.getRolId() + " no pertenece a la organización con ID " + organizacionId + ".");
            }
        });

        // Verificar si el nuevo email ya existe para esta organización (y no es el mismo usuario)
        if (!existingUsuario.getEmail().equals(request.getEmail()) &&
                usuarioDao.existsByOrganizacionIdAndEmail(organizacionId, request.getEmail())) {
            throw new DuplicateResourceException("Ya existe otro usuario con el email '" + request.getEmail() + "' en la organización con ID: " + organizacionId);
        }

        existingUsuario.setNombreCompleto(request.getNombreCompleto());
        existingUsuario.setEmail(request.getEmail());
        // Solo actualizar contraseña si se provee una nueva
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUsuario.setPasswordHash(passwordHasher.hashPassword(request.getPassword()));
        }
        existingUsuario.setRolId(request.getRolId());
        if (request.getActivo() != null) {
            existingUsuario.setActivo(request.getActivo());
        }

        try {
            Usuario updatedUsuario = usuarioDao.update(existingUsuario);
            log.info("Usuario con ID {} actualizado exitosamente para la organización ID {}.", id, organizacionId);
            return UsuarioMapper.toResponse(updatedUsuario);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al actualizar usuario con ID {}: {}", id, e.getMessage(), e);
            throw new IllegalArgumentException("Error al actualizar el usuario. Verifique los datos (email, rol) e intente de nuevo.");
        }
    }

    @Override
    @Transactional
    public void deleteUsuario(Long id, Long organizacionId) {
        log.info("Intentando eliminar usuario con ID {} para organización ID {}.", id, organizacionId);

        Usuario existingUsuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Asegurarse de que el usuario pertenece a la organización correcta
        if (!existingUsuario.getOrganizacionId().equals(organizacionId)) {
            throw new AuthorizationException("El usuario con ID " + id + " no pertenece a la organización ID " + organizacionId + ".");
        }

        boolean deleted = usuarioDao.deleteById(id); // Este deleteById ya valida el tenant
        if (!deleted) {
            // Esto solo ocurriría si el usuario se borra entre la validación y el delete
            throw new ResourceNotFoundException("Fallo al eliminar el usuario con ID: " + id);
        }
        log.info("Usuario con ID {} eliminado exitosamente para organización ID {}.", id, organizacionId);
    }

    @Override
    public UsuarioResponse getUsuarioByEmail(Long organizacionId, String email) {
        log.debug("Buscando usuario con email '{}' en organización ID: {}", email, organizacionId);
        if (!organizacionDao.existsById(organizacionId)) {
            throw new ResourceNotFoundException("Organización no encontrada con ID: " + organizacionId);
        }
        Usuario usuario = usuarioDao.findByOrganizacionIdAndEmail(organizacionId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email '" + email + "' no encontrado para la organización ID: " + organizacionId));
        return UsuarioMapper.toResponse(usuario);
    }
}
