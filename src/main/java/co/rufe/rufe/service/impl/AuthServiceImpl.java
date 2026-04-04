package co.rufe.rufe.service.impl;

import java.util.Collection;

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
    public AuthServiceImpl(IUsuarioDao usuarioDao, AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider) {
        this.usuarioDao = usuarioDao;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para email: {}", request.getEmail());

        // 1. Autenticar usando Spring Security's AuthenticationManager
        // Esto lanzará una AuthenticationException si las credenciales son inválidas
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Fallo de autenticación para email {}: {}", request.getEmail(), e.getMessage());
            // Proporciona un mensaje genérico para seguridad
            throw new AuthenticationException("Credenciales inválidas (email, contraseña o organización).");
        }

        // Si la autenticación fue exitosa, el SecurityContextHolder ya tendrá la
        // autenticación.
        // Pero necesitamos los detalles completos del usuario para el JWT y la
        // respuesta.
        // La ventaja de hacer la búsqueda aquí es que ya tenemos el email del usuario
        // validado por Spring Security.
        // También nos permite validar el estado activo de usuario y organización antes
        // de emitir el token.

        // Buscamos los detalles completos del usuario (incluyendo ID de organización y
        // rol)
        // usando solo el email, ya que la autenticación fue exitosa.
        UsuarioWithDetails usuarioDetails = usuarioDao.findUserWithDetailsByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Usuario autenticado pero detalles no encontrados para email: {}", request.getEmail());
                    return new AuthenticationException("Error interno de autenticación. Contacte al administrador.");
                });

        // Validaciones de estado activo (importante después de encontrar los detalles)
        if (!usuarioDetails.getActivo()) {
            throw new AuthenticationException("El usuario se encuentra inactivo. Contacte a su administrador.");
        }

        if (!usuarioDetails.getOrganizacionActiva()) {
            throw new AuthenticationException("La organización se encuentra inactiva. Contacte a su administrador.");
        }

        // El TenantContext debe establecerse en el JwtAuthenticationFilter para todas
        // las solicitudes subsiguientes.
        // Sin embargo, para el proceso de login en sí, una vez que el usuario y la
        // organización son validados,
        // podemos establecerlo para cualquier operación inmediata que necesite el
        // contexto,
        // aunque el filtro lo manejará para las API calls protegidas.
        TenantContext.setCurrentOrganizationId(usuarioDetails.getOrganizacionId());
        log.debug("TenantContext establecido para usuario {}: {}", request.getEmail(),
                usuarioDetails.getOrganizacionId());

        // Obtener las autoridades (roles y permisos) del objeto Authentication que
        // Spring Security ha populado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Generar el token JWT incluyendo todas las autoridades y claims enriquecidos
        String jwt = jwtTokenProvider.generateToken(authentication, authorities,
                usuarioDetails.getId(),
                usuarioDetails.getRolId(),
                usuarioDetails.getRolNombre(),
                usuarioDetails.getNombreCompleto(),
                usuarioDetails.getOrganizacionId());

        log.info("Login exitoso para usuario: {}", request.getEmail());

        // Limpiamos el TenantContext si no se va a usar inmediatamente después del
        // login en la misma hebra
        // para evitar fugas. El filtro de seguridad lo reestablecerá para peticiones
        // subsecuentes.
        TenantContext.clear();

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .user(AuthResponse.UserAuthDTO.builder()
                        .id(usuarioDetails.getId())
                        .nombre(usuarioDetails.getNombreCompleto())
                        .rol(usuarioDetails.getRolNombre())
                        .rolId(usuarioDetails.getRolId())
                        .organizacionId(usuarioDetails.getOrganizacionId())
                        .build())
                .build();
    }
}