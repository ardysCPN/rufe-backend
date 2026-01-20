package co.rufe.rufe.security;

import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.dao.IPermisoDao;
import co.rufe.rufe.model.Rol;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.model.Permiso;
import co.rufe.rufe.util.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioDao usuarioDao;
    private final IRolDao rolDao;
    private final IPermisoDao permisoDao;

    public CustomUserDetailsService(IUsuarioDao usuarioDao, IRolDao rolDao, IPermisoDao permisoDao) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
        this.permisoDao = permisoDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        if (usuario.getOrganizacionId() == null) {
            log.error("Usuario {} encontrado sin un ID de organización asociado.", email);
            throw new UsernameNotFoundException("Usuario no asociado con una organización.");
        }

        TenantContext.setCurrentOrganizationId(usuario.getOrganizacionId());
        log.debug("TenantContext.CurrentOrganizationId establecido para usuario {}: {}", email, usuario.getOrganizacionId());

        Rol rol = rolDao.findById(usuario.getRolId())
                .orElseThrow(() -> {
                    log.error("Rol no encontrado con ID: {}", usuario.getRolId());
                    return new UsernameNotFoundException("Rol no encontrado para el usuario.");
                });

        List<Permiso> permisos = permisoDao.findByRolId(rol.getId());
        log.debug("Permisos cargados para el rol {}: {}", rol.getNombreRol(),
        permisos.stream().map(Permiso::getNombrePermiso).collect(Collectors.joining(", ")));

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // Añadir el rol del usuario como una autoridad (convención "ROLE_")
        // Usar .toUpperCase() en el nombre del rol es una convención común, asegúrate de que esto coincida
        // con cómo esperas validar roles con hasRole().
        authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol().toUpperCase()));
        
        // Añadir cada permiso como una autoridad, usando directamente el nombre_permiso
        // que viene de la base de datos (ej: "organizaciones:crear")
        permisos.forEach(permiso -> authorities.add(new SimpleGrantedAuthority(permiso.getNombrePermiso())));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                authorities
        );
    }
}