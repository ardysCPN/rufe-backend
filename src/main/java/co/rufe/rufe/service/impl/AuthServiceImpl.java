package co.rufe.rufe.service.impl;


import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.dto.auth.AuthResponse;
import co.rufe.rufe.dto.auth.LoginRequest;
import co.rufe.rufe.exception.AuthenticationException;
import co.rufe.rufe.model.UsuarioWithDetails;
import co.rufe.rufe.service.IAuthService;
import co.rufe.rufe.security.JwtTokenProvider;
import co.rufe.rufe.util.PasswordHasher;
import co.rufe.rufe.util.TenantContext; // Para establecer el contexto del tenant después del login
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioDao usuarioDao;
    private final PasswordHasher passwordHasher;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(IUsuarioDao usuarioDao, PasswordHasher passwordHasher, JwtTokenProvider jwtTokenProvider) {
        this.usuarioDao = usuarioDao;
        this.passwordHasher = passwordHasher;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional(readOnly = true) // La operación de login es de solo lectura
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para email: {} en organización: {}", request.getEmail(), request.getOrganizacion());

        // Usamos la función de BD para obtener todos los detalles necesarios en una sola consulta
        UsuarioWithDetails usuarioDetails = usuarioDao.findUserWithDetailsByEmailAndOrganizationName(request.getEmail(), request.getOrganizacion())
                .orElseThrow(() -> new AuthenticationException("Credenciales inválidas (email, organización o contraseña)."));

        if (!passwordHasher.verifyPassword(request.getPassword(), usuarioDetails.getPasswordHash())) {
            throw new AuthenticationException("Credenciales inválidas (email, organización o contraseña).");
        }

        if (!usuarioDetails.getActivo()) {
            throw new AuthenticationException("El usuario se encuentra inactivo. Contacte a su administrador.");
        }

        if (!usuarioDetails.getOrganizacionActiva()) {
            throw new AuthenticationException("La organización se encuentra inactiva. Contacte a su administrador.");
        }

        // Establecer el TenantContext una vez el usuario está autenticado y la organización es válida
        TenantContext.setCurrentOrganizationId(usuarioDetails.getOrganizacionId());

        // Obtener los IDs de los items de menú para este usuario (basado en su rol)
        // Esto se puede hacer aquí o en el CustomUserDetailsService de Spring Security
        // Por ahora, lo hacemos aquí para el JWT, si necesitamos roles/permisos complejos, el UserDetailsService es mejor.
        // Asumimos que el JWT solo necesita el rol y el organizacionId.
        // Si los permisos específicos son necesarios en el JWT, se obtendrían aquí y se añadirían como claims.
        // Por simplicidad inicial, el JWT solo contendrá organizacionId, userId y rolId.

        // Generar el token JWT
        String jwt = jwtTokenProvider.generateToken(
                usuarioDetails.getOrganizacionId(),
                usuarioDetails.getId(),
                usuarioDetails.getRolNombre(),
                usuarioDetails.getEmail()
        );

        log.info("Login exitoso para usuario: {} en organización: {}", request.getEmail(), request.getOrganizacion());

        // Limpiar el TenantContext después de generar el token para no afectar operaciones posteriores
        // que no se ejecuten bajo un filtro de seguridad. Sin embargo, el filtro de seguridad lo establecerá
        // para cada solicitud subsecuente que use el token.
        // TenantContext.clear(); // Opcional, dependiendo de cuándo el filtro de seguridad lo establezca.

        return new AuthResponse(
                jwt,
                "Bearer", // Tipo de token
                usuarioDetails.getId(),
                usuarioDetails.getEmail(),
                usuarioDetails.getOrganizacionNombre(),
                usuarioDetails.getRolId(),
                usuarioDetails.getRolNombre()
        );
    }
}
