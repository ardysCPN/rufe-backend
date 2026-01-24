package co.rufe.rufe.controller;

import java.util.Collection; // Nuevo import
import java.util.List;
import java.util.stream.Collectors; // Nuevo import

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Nuevo
import org.springframework.security.core.GrantedAuthority; // Nuevo
import org.springframework.security.core.context.SecurityContextHolder; // Nuevo
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.rufe.rufe.dto.menu.MenuItemRequest;
import co.rufe.rufe.dto.menu.MenuItemResponse;
import co.rufe.rufe.dto.permiso.PermisoResponse;
import co.rufe.rufe.security.SecurityUtils;
import co.rufe.rufe.service.IMenuItemService;
import jakarta.validation.Valid; // Asegúrate de usar jakarta.validation si es Spring Boot 3+
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/organizaciones/{organizacionId}/menu-items")
@Slf4j
public class MenuItemController {

    private final IMenuItemService menuItemService;
    private final SecurityUtils securityUtils; // Inyectar SecurityUtils

    public MenuItemController(IMenuItemService menuItemService, SecurityUtils securityUtils) {
        this.menuItemService = menuItemService;
        this.securityUtils = securityUtils; // Inicializar
    }

    // --- Endpoints de Gestión de MenuItem (CRUD para Administradores Globales) ---

    @PostMapping
    // Crear MenuItem: Esta operación solo debe ser realizada por un ADMIN_GLOBAL
    // ya que los items de menú son globales (no por organización).
    // El {organizacionId} en la ruta es solo para mantener la coherencia del path
    // general,
    // pero la lógica de este endpoint no lo usa para filtrar el item de menú.
    @PreAuthorize("(hasAuthority('organizaciones:crear') or hasAuthority('menu:crear')) and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @PathVariable Long organizacionId, // Se mantiene en el path, pero el item de menú no se asocia a ella
            @Valid @RequestBody MenuItemRequest request) {
        log.info("Solicitud para crear un ítem de menú: {} (Global Operation)", request.getNombreItem());
        MenuItemResponse response = menuItemService.createMenuItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{menuItemId}")
    // Leer MenuItem por ID: Puede ser leído por cualquier ADMIN_GLOBAL, o
    // ADMIN_ORGANIZACION
    // con el permiso 'menu:leer' y que pertenezca a la organización.
    @PreAuthorize("hasAuthority('menu:leer') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @PathVariable Long organizacionId,
            @PathVariable Integer menuItemId) { // Tipo de ID ajustado
        log.info("Solicitud para obtener ítem de menú con ID: {} en contexto de organización ID: {}", menuItemId,
                organizacionId);
        MenuItemResponse response = menuItemService.getMenuItemById(menuItemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/nombre/{nombreItem}")
    // Leer MenuItem por nombre: Permiso 'menu:leer' y pertenencia a la
    // organización.
    @PreAuthorize("hasAuthority('menu:leer') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<MenuItemResponse> getMenuItemByNombre(
            @PathVariable Long organizacionId,
            @PathVariable String nombreItem) {
        log.info("Solicitud para obtener ítem de menú con nombre: {} en contexto de organización ID: {}", nombreItem,
                organizacionId);
        MenuItemResponse response = menuItemService.getMenuItemByNombre(nombreItem);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/parent/{parentId}")
    // Obtener sub-ítems: Permiso 'menu:leer' y pertenencia a la organización.
    @PreAuthorize("hasAuthority('menu:leer') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<MenuItemResponse>> getSubMenuItems(
            @PathVariable Long organizacionId,
            @PathVariable Integer parentId) { // Tipo de ID ajustado
        log.info("Solicitud para obtener sub-ítems de menú para parentId: {} en contexto de organización ID: {}",
                parentId, organizacionId);
        List<MenuItemResponse> responses = menuItemService.getSubMenuItems(parentId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/all") // Añadido un path para evitar conflicto con / si hay otros @GetMapping sin path
    // Listar todos los MenuItems (para la gestión interna): Permiso 'menu:leer' y
    // pertenencia a la organización.
    @PreAuthorize("hasAuthority('menu:leer') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems(
            @PathVariable Long organizacionId) {
        log.info("Solicitud para obtener todos los ítems de menú en contexto de organización ID: {}", organizacionId);
        List<MenuItemResponse> responses = menuItemService.getAllMenuItems();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{menuItemId}")
    // Actualizar MenuItem: Operación global, solo para ADMIN_GLOBAL.
    @PreAuthorize("hasAuthority('menu:actualizar') and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long organizacionId,
            @PathVariable Integer menuItemId, // Tipo de ID ajustado
            @Valid @RequestBody MenuItemRequest request) {
        log.info("Solicitud para actualizar ítem de menú con ID: {} en contexto de organización ID: {}", menuItemId,
                organizacionId);
        MenuItemResponse response = menuItemService.updateMenuItem(menuItemId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{menuItemId}")
    // Eliminar MenuItem: Operación global, solo para ADMIN_GLOBAL.
    @PreAuthorize("hasAuthority('menu:eliminar') and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long organizacionId,
            @PathVariable Integer menuItemId) { // Tipo de ID ajustado
        log.info("Solicitud para eliminar ítem de menú con ID: {} en contexto de organización ID: {}", menuItemId,
                organizacionId);
        menuItemService.deleteMenuItem(menuItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints para Gestión de Permisos ASOCIADOS a un MenuItem (Visibilidad)
    // ---
    // NOTA: Estos endpoints gestionan qué permisos se requieren para *ver* un
    // MenuItem,
    // NO asignan MenuItems a Roles. La asignación de permisos a Roles se
    // gestionaría
    // en un controlador de Roles/Permisos.

    @PostMapping("/{menuItemId}/permisos/{permisoId}")
    // Asignar Permiso a MenuItem: Solo ADMIN_GLOBAL puede hacer esto.
    @PreAuthorize("hasAuthority('menu:asignar_permisos') and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<Void> assignPermisoToMenuItem(
            @PathVariable Long organizacionId, // Se mantiene por el path general
            @PathVariable Integer menuItemId, // Tipo de ID ajustado
            @PathVariable Integer permisoId) { // Tipo de ID ajustado
        log.info("Asignando Permiso ID: {} a MenuItem ID: {} en organización ID: {}", permisoId, menuItemId,
                organizacionId);
        menuItemService.assignPermisoToMenuItem(menuItemId, permisoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{menuItemId}/permisos/{permisoId}")
    // Revocar Permiso de MenuItem: Solo ADMIN_GLOBAL puede hacer esto.
    @PreAuthorize("hasAuthority('menu:asignar_permisos') and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<Void> revokePermisoFromMenuItem(
            @PathVariable Long organizacionId, // Se mantiene por el path general
            @PathVariable Integer menuItemId, // Tipo de ID ajustado
            @PathVariable Integer permisoId) { // Tipo de ID ajustado
        log.info("Revocando Permiso ID: {} de MenuItem ID: {} en organización ID: {}", permisoId, menuItemId,
                organizacionId);
        menuItemService.revokePermisoFromMenuItem(menuItemId, permisoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{menuItemId}/permisos")
    // Obtener Permisos asociados a un MenuItem: Permiso 'menu:leer' y pertenencia a
    // la organización.
    @PreAuthorize("hasAuthority('menu:leer') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<PermisoResponse>> getPermisosByMenuItemId(
            @PathVariable Long organizacionId, // Se mantiene por el path general
            @PathVariable Integer menuItemId) { // Tipo de ID ajustado
        log.info("Solicitud para obtener Permisos asociados a MenuItem ID: {} en organización ID: {}", menuItemId,
                organizacionId);
        List<PermisoResponse> responses = menuItemService.getPermisosByMenuItemId(menuItemId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // --- Endpoint para Obtener el Menú Dinámico del Usuario ---
    // Este endpoint es fundamental para el frontend, y NO debe estar dentro de
    // /{organizacionId}/menu-items
    // ya que opera sobre los permisos globales del usuario, no los permisos de una
    // organización específica para el menú.
    // Además, el filtro de seguridad ya habrá establecido el TenantContext si el
    // JWT es válido.

    // Un endpoint más adecuado sería /api/menu/dynamic o /api/users/me/menu
    @GetMapping("/dynamic-menu")
    // Obtener el menú dinámico para el usuario autenticado.
    // Solo requiere que el usuario esté autenticado y en la organización adecuada.
    // La filtración del menú se hace *dentro* del servicio basado en los permisos
    // del usuario.
    // NOTA: El `organizacionId` en la URL es redundante aquí porque el usuario ya
    // tiene su organización
    // en el token, pero se mantiene por consistencia con la ruta base si se desea.
    @PreAuthorize("isAuthenticated() and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<MenuItemResponse>> getDynamicMenuForAuthenticatedUser(
            @PathVariable Long organizacionId) {
        log.info("Solicitud para obtener el menú dinámico para el usuario autenticado en organización ID: {}",
                organizacionId);

        // Obtener los permisos del usuario autenticado desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userPermissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // El servicio se encargará de filtrar el menú basado en estos permisos
        List<MenuItemResponse> dynamicMenu = menuItemService.getDynamicMenuForUser(userPermissions);
        return new ResponseEntity<>(dynamicMenu, HttpStatus.OK);
    }
}