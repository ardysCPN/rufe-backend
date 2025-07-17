package co.rufe.rufe.security;

import co.rufe.rufe.util.TenantContext;
import co.rufe.rufe.dao.IMenuItemDao;
import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.dao.IRolPermisoDao; // ¡Importante!
import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.model.MenuItem;
import co.rufe.rufe.model.RolPermiso; // Necesario
import co.rufe.rufe.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioDao usuarioDao;
    private final IRolDao rolDao;
    private final IRolPermisoDao rolPermisoDao; // Inyectado
    private final IMenuItemDao menuItemDao;     // Inyectado

    public CustomUserDetailsService(IUsuarioDao usuarioDao, IRolDao rolDao,
                                    IRolPermisoDao rolPermisoDao, IMenuItemDao menuItemDao) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
        this.rolPermisoDao = rolPermisoDao;
        this.menuItemDao = menuItemDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario;
        Long currentOrgId = TenantContext.getCurrentOrganizationId();

        if (currentOrgId != null) {
            usuario = usuarioDao.findByOrganizacionIdAndEmail(currentOrgId, email)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found with email: " + email + " in organization ID: " + currentOrgId));
        } else {
            usuario = usuarioDao.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            if (usuario.getOrganizacionId() == null) {
                log.error("User {} found without an associated organization ID.", email);
                throw new UsernameNotFoundException("User not associated with an organization.");
            }
            TenantContext.setCurrentOrganizationId(usuario.getOrganizacionId());
        }

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("User is inactive: " + email);
        }

        // Obtener el nombre del rol del usuario (si aún quieres ROLE_ prefix)
        String roleName = rolDao.findById(usuario.getRolId())
                .map(co.rufe.rufe.model.Rol::getNombreRol)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found for user: " + email));

        // Obtener los IDs de los MenuItems asociados a este rol a través de la tabla rol_permisos
        Set<Long> menuItemIds = rolPermisoDao.findByRolId(usuario.getRolId()).stream()
                .map(RolPermiso::getMenuItemId)
                .collect(Collectors.toSet());

        // Obtener los objetos MenuItem completos usando los IDs
        List<MenuItem> permisos = menuItemDao.findByIds(menuItemIds);

        // Convertir los MenuItems (permisos) en GrantedAuthorities
        Collection<GrantedAuthority> authorities = permisos.stream()
                .map(menuItem -> new SimpleGrantedAuthority(menuItem.getNombreItem().toUpperCase())) // ¡Usamos nombre_item como autoridad!
                .collect(Collectors.toSet());

        // Opcional: También añade el rol como una autoridad (ROLE_NOMBRE_ROL) si lo necesitas para hasRole() en algún lugar
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));

        return new User(usuario.getEmail(),
                        usuario.getPasswordHash(),
                        authorities);
    }
}