package co.rufe.rufe.service.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager; // Nuevo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Nuevo
import org.springframework.security.core.Authentication; // Nuevo
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.dto.auth.AuthResponse;
import co.rufe.rufe.dto.auth.LoginRequest;
import co.rufe.rufe.exception.AuthenticationException; // Asegúrate de que esta excepción exista
import co.rufe.rufe.model.UsuarioWithDetails; // DTO para detalles de usuario combinados
import co.rufe.rufe.security.JwtTokenProvider;
import co.rufe.rufe.service.IAuthService;
import co.rufe.rufe.util.TenantContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioDao usuarioDao; // Aún lo necesitamos para buscar detalles adicionales
    private final AuthenticationManager authenticationManager; // ¡Nuevo! Gestiona la autenticación
    private final JwtTokenProvider jwtTokenProvider;

    // Ya no necesitamos PasswordHasher aquí porque AuthenticationManager lo maneja
    public AuthServiceImpl(IUsuarioDao usuarioDao, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.usuarioDao = usuarioDao;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para email: {} en organización: {}", request.getEmail(), request.getOrganizacion());

        // 1. Autenticar usando Spring Security's AuthenticationManager
        // Esto lanzará una AuthenticationException si las credenciales son inválidas
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Fallo de autenticación para email {}: {}", request.getEmail(), e.getMessage());
            // Proporciona un mensaje genérico para seguridad
            throw new AuthenticationException("Credenciales inválidas (email, contraseña o organización).");
        }

        // Si la autenticación fue exitosa, el SecurityContextHolder ya tendrá la autenticación.
        // Pero necesitamos los detalles completos del usuario para el JWT y la respuesta.
        // La ventaja de hacer la búsqueda aquí es que ya tenemos el email del usuario validado por Spring Security.
        // También nos permite validar el estado activo de usuario y organización antes de emitir el token.

        // NOTA: Es crucial que tu CustomUserDetailsService establezca el TenantContext *antes*
        // de intentar cargar el usuario por email. O si el `findByEmail` no usa el TenantContext,
        // necesitamos un `findByEmailAndOrganizacionNombre` que no dependa del TenantContext explícito
        // en esta primera fase de login.
        // Asumiendo que `findUserWithDetailsByEmailAndOrganizationName` es la forma correcta de buscar
        // al usuario y su organización en el login inicial.
        UsuarioWithDetails usuarioDetails = usuarioDao.findUserWithDetailsByEmailAndOrganizationName(request.getEmail(), request.getOrganizacion())
                .orElseThrow(() -> {
                    // Esto no debería ocurrir si authenticationManager ya autenticó,
                    // a menos que haya una inconsistencia de datos o un problema con la consulta.
                    log.error("Usuario autenticado pero detalles no encontrados o organización incorrecta para email: {}", request.getEmail());
                    return new AuthenticationException("Error interno de autenticación. Contacte al administrador.");
                });

        // Validaciones de estado activo (importante después de encontrar los detalles)
        if (!usuarioDetails.getActivo()) {
            throw new AuthenticationException("El usuario se encuentra inactivo. Contacte a su administrador.");
        }

        if (!usuarioDetails.getOrganizacionActiva()) {
            throw new AuthenticationException("La organización se encuentra inactiva. Contacte a su administrador.");
        }

        // El TenantContext debe establecerse en el JwtAuthenticationFilter para todas las solicitudes subsiguientes.
        // Sin embargo, para el proceso de login en sí, una vez que el usuario y la organización son validados,
        // podemos establecerlo para cualquier operación inmediata que necesite el contexto,
        // aunque el filtro lo manejará para las API calls protegidas.
        TenantContext.setCurrentOrganizationId(usuarioDetails.getOrganizacionId());
        log.debug("TenantContext establecido para usuario {}: {}", request.getEmail(), usuarioDetails.getOrganizacionId());

        // Obtener las autoridades (roles y permisos) del objeto Authentication que Spring Security ha populado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Generar el token JWT incluyendo todas las autoridades
        String jwt = jwtTokenProvider.generateToken(authentication, authorities);

        log.info("Login exitoso para usuario: {} en organización: {}", request.getEmail(), request.getOrganizacion());

        // Limpiamos el TenantContext si no se va a usar inmediatamente después del login en la misma hebra
        // para evitar fugas. El filtro de seguridad lo reestablecerá para peticiones subsecuentes.
        TenantContext.clear();

        return new AuthResponse(
                jwt,
                "Bearer",
                usuarioDetails.getId(),
                usuarioDetails.getEmail(),
                usuarioDetails.getOrganizacionId(),
                usuarioDetails.getOrganizacionNombre(),
                usuarioDetails.getRolId(),
                usuarioDetails.getRolNombre(),
                // Incluir los permisos en la respuesta si el frontend los necesita directamente
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}