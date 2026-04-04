package co.rufe.rufe.security;

import java.util.Optional;

import org.springframework.stereotype.Component;

import co.rufe.rufe.dao.IOrganizacionDao;
import co.rufe.rufe.dao.IRolDao;
import co.rufe.rufe.dao.IUsuarioDao;
import co.rufe.rufe.model.Organizacion;
import co.rufe.rufe.model.Usuario;
import co.rufe.rufe.util.TenantContext; // Importar TenantContext
import lombok.extern.slf4j.Slf4j;

@Component("securityUtils") // Importante: el nombre del bean para usarlo en @PreAuthorize
@Slf4j
public class SecurityUtils {

    private final IUsuarioDao usuarioDao;
    private final IOrganizacionDao organizacionDao;
    private final IRolDao rolDao;

    public SecurityUtils(IUsuarioDao usuarioDao, IOrganizacionDao organizacionDao, IRolDao rolDao) {
        this.usuarioDao = usuarioDao;
        this.organizacionDao = organizacionDao;
        this.rolDao = rolDao;
    }

    /**
     * Obtiene el ID de la organización del usuario autenticado desde el
     * TenantContext.
     * Esto asume que el TenantContext ha sido establecido por el
     * JwtAuthenticationFilter.
     * 
     * @return ID de la organización actual o null si no hay un usuario autenticado
     *         o no hay ID de organización.
     */
    public Long getCurrentUserOrganizationId() {
        return TenantContext.getCurrentOrganizationId();
    }

    /**
     * Verifica si el usuario autenticado pertenece a la organización con el ID
     * dado.
     * Usado en @PreAuthorize para endpoints a nivel de organización.
     * 
     * @param organizacionId El ID de la organización a verificar.
     * @return true si el usuario pertenece a la organización, false en caso
     *         contrario.
     */
    public boolean isUserInOrganization(Long organizacionId) {
        Long currentUserOrgId = getCurrentUserOrganizationId();
        if (currentUserOrgId == null) {
            log.warn("isUserInOrganization: No hay ID de organización en el contexto de seguridad.");
            return false;
        }
        boolean isAuthorized = currentUserOrgId.equals(organizacionId);
        log.debug("isUserInOrganization({}): Usuario en organización {} -> {}", organizacionId, currentUserOrgId,
                isAuthorized);
        return isAuthorized;
    }

    /**
     * Verifica si el usuario autenticado pertenece a la organización con el nombre
     * dado.
     * Usado en @PreAuthorize para endpoints a nivel de organización por nombre.
     * 
     * @param organizacionNombre El nombre de la organización a verificar.
     * @return true si el usuario pertenece a la organización, false en caso
     *         contrario.
     */
    public boolean isUserInOrganizationByName(String organizacionNombre) {
        Long currentUserOrgId = getCurrentUserOrganizationId();
        if (currentUserOrgId == null) {
            log.warn("isUserInOrganizationByName: No hay ID de organización en el contexto de seguridad.");
            return false;
        }
        Optional<Organizacion> organizacion = organizacionDao.findByNombreOrganizacion(organizacionNombre);
        boolean isAuthorized = organizacion.map(org -> org.getId().equals(currentUserOrgId)).orElse(false);
        log.debug("isUserInOrganizationByName({}): Usuario en organización {} -> {}", organizacionNombre,
                currentUserOrgId, isAuthorized);
        return isAuthorized;
    }

    /**
     * Verifica si el usuario autenticado pertenece a la misma organización que el
     * rol especificado.
     * 
     * @param rolId          El ID del rol a verificar.
     * @param organizacionId El ID de la organización proporcionado en la ruta (para
     *                       doble verificación).
     * @return true si el rol pertenece a la misma organización del usuario, false
     *         en caso contrario.
     */
    public boolean isUserInRoleOrganization(Long rolId, Long organizacionId) {
        Long currentUserOrgId = getCurrentUserOrganizationId();
        if (currentUserOrgId == null) {
            log.warn("isUserInRoleOrganization: No hay ID de organización en el contexto de seguridad.");
            return false;
        }
        if (!currentUserOrgId.equals(organizacionId)) {
            log.warn("isUserInRoleOrganization: ID de organización de ruta ({}) no coincide con ID de usuario ({}).",
                    organizacionId, currentUserOrgId);
            return false;
        }

        return rolDao.findById(rolId)
                .map(rol -> rol.getOrganizacionId().equals(currentUserOrgId))
                .orElseGet(() -> {
                    log.warn("isUserInRoleOrganization: Rol con ID {} no encontrado.", rolId);
                    return false;
                });
    }

    /**
     * Verifica si el usuario autenticado pertenece a la misma organización que el
     * usuario objetivo.
     * También verifica que el ID de la organización en la ruta coincida con el del
     * usuario autenticado.
     * 
     * @param targetUserId       El ID del usuario objetivo a verificar.
     * @param pathOrganizacionId El ID de la organización proporcionado en la ruta.
     * @return true si el usuario autenticado está en la misma organización que el
     *         usuario objetivo
     *         y si el ID de la organización de la ruta coincide con el ID de la
     *         organización del usuario autenticado.
     */
    public boolean isUserInTargetOrganization(Long targetUserId, Long pathOrganizacionId) {
        Long currentUserOrgId = getCurrentUserOrganizationId();
        if (currentUserOrgId == null) {
            log.warn("isUserInTargetOrganization: No hay ID de organización en el contexto de seguridad.");
            return false;
        }
        if (!currentUserOrgId.equals(pathOrganizacionId)) {
            log.warn("isUserInTargetOrganization: ID de organización de ruta ({}) no coincide con ID de usuario ({}).",
                    pathOrganizacionId, currentUserOrgId);
            return false;
        }

        return usuarioDao.findById(targetUserId)
                .map(targetUser -> targetUser.getOrganizacionId().equals(currentUserOrgId))
                .orElseGet(() -> {
                    log.warn("isUserInTargetOrganization: Usuario objetivo con ID {} no encontrado.", targetUserId);
                    return false;
                });
    }

    // Método para verificar si el usuario autenticado pertenece a la organización
    // y si el usuario del path pertenece a esa misma organización.
    public boolean isUserInUserOrganization(Long usuarioId, Long organizacionId) {
        // 1. Verificar si el usuario autenticado pertenece a la organizacionId del path
        if (!isUserInOrganization(organizacionId)) {
            return false;
        }

        // 2. Verificar si el usuario con usuarioId del path realmente pertenece a la
        // organizacionId del path
        Optional<Usuario> targetUser = usuarioDao.findById(usuarioId);
        return targetUser.map(usuario -> usuario.getOrganizacionId().equals(organizacionId)).orElse(false);
    }

    // Método para verificar si el usuario autenticado pertenece a la organización
    // y si el email del path corresponde a un usuario dentro de esa organización.
    public boolean isUserInOrganizationAndEmailMatches(Long organizacionId, String email) {
        // 1. Verificar si el usuario autenticado pertenece a la organizacionId del path
        if (!isUserInOrganization(organizacionId)) {
            return false;
        }

        // 2. Verificar si el usuario con el email del path realmente pertenece a la
        // organizacionId del path
        Optional<Usuario> targetUser = usuarioDao.findByOrganizacionIdAndEmail(organizacionId, email);
        return targetUser.isPresent();
    }

    /**
     * Verifica si el usuario autenticado tiene el rol de administrador global.
     * 
     * @return true si el usuario tiene el rol ADMIN_GLOBAL, false en caso
     *         contrario.
     */
    public boolean isGlobalAdmin() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_GLOBAL"));
    }
}
