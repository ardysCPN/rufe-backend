package co.rufe.rufe.controller;

import co.rufe.rufe.dto.menu.MenuItemRequest;
import co.rufe.rufe.dto.menu.MenuItemResponse;
import co.rufe.rufe.service.IMenuItemService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones/{organizacionId}/menu-items")
@Slf4j
public class MenuItemController {

    private final IMenuItemService menuItemService;

    public MenuItemController(IMenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping
    // Crear MenuItem: Operación global, solo para ADMIN_GLOBAL.
    @PreAuthorize("hasAuthority('MENU_ITEM_CREATE') and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<MenuItemResponse> createMenuItem(@PathVariable Long organizacionId, @Valid @RequestBody MenuItemRequest request) {
        log.info("Solicitud para crear un ítem de menú: {} en contexto de organización ID: {}", request.getNombreItem(), organizacionId);
        MenuItemResponse response = menuItemService.createMenuItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{menuItemId}")
    // Leer MenuItem por ID: Permiso de lectura y pertenencia a la organización.
    @PreAuthorize("hasAuthority('MENU_ITEM_READ') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<MenuItemResponse> getMenuItemById(@PathVariable Long organizacionId, @PathVariable Long menuItemId) {
        log.info("Solicitud para obtener ítem de menú con ID: {} en contexto de organización ID: {}", menuItemId, organizacionId);
        MenuItemResponse response = menuItemService.getMenuItemById(menuItemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/nombre/{nombreItem}")
    // Leer MenuItem por nombre: Permiso de lectura y pertenencia a la organización.
    @PreAuthorize("hasAuthority('MENU_ITEM_READ') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<MenuItemResponse> getMenuItemByNombre(@PathVariable Long organizacionId, @PathVariable String nombreItem) {
        log.info("Solicitud para obtener ítem de menú con nombre: {} en contexto de organización ID: {}", nombreItem, organizacionId);
        MenuItemResponse response = menuItemService.getMenuItemByNombre(nombreItem);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/parent/{parentId}")
    // Obtener sub-ítems: Permiso de lectura y pertenencia a la organización.
    @PreAuthorize("hasAuthority('MENU_ITEM_READ') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<MenuItemResponse>> getSubMenuItems(@PathVariable Long organizacionId, @PathVariable Long parentId) {
        log.info("Solicitud para obtener sub-ítems de menú para parentId: {} en contexto de organización ID: {}", parentId, organizacionId);
        List<MenuItemResponse> responses = menuItemService.getSubMenuItems(parentId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping
    // Listar todos los MenuItems: Permiso de lista y pertenencia a la organización.
    @PreAuthorize("hasAuthority('MENU_ITEM_LIST') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems(@PathVariable Long organizacionId) {
        log.info("Solicitud para obtener todos los ítems de menú en contexto de organización ID: {}", organizacionId);
        List<MenuItemResponse> responses = menuItemService.getAllMenuItems();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{menuItemId}")
    // Actualizar MenuItem: Operación global, solo para ADMIN_GLOBAL.
    @PreAuthorize("hasAuthority('MENU_ITEM_UPDATE') and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable Long organizacionId, @PathVariable Long menuItemId, @Valid @RequestBody MenuItemRequest request) {
        log.info("Solicitud para actualizar ítem de menú con ID: {} en contexto de organización ID: {}", menuItemId, organizacionId);
        MenuItemResponse response = menuItemService.updateMenuItem(menuItemId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{menuItemId}")
    // Eliminar MenuItem: Operación global, solo para ADMIN_GLOBAL.
    @PreAuthorize("hasAuthority('MENU_ITEM_DELETE') and hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long organizacionId, @PathVariable Long menuItemId) {
        log.info("Solicitud para eliminar ítem de menú con ID: {} en contexto de organización ID: {}", menuItemId, organizacionId);
        menuItemService.deleteMenuItem(menuItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Métodos para asignar/revocar permisos de MenuItem a un Rol
    @PostMapping("/{menuItemId}/assignToRole/{rolId}")
    // Asignar permiso a rol: Requiere permiso de asignación y que el usuario esté en la organización del rol.
    @PreAuthorize("hasAuthority('MENU_ITEM_ASSIGN') and @securityUtils.isUserInRoleOrganization(#rolId, #organizacionId)")
    public ResponseEntity<Void> assignMenuItemToRole(@PathVariable Long organizacionId, @PathVariable Long menuItemId, @PathVariable Long rolId) {
        log.info("Asignando MenuItem ID: {} a Rol ID: {} en organización ID: {}", menuItemId, rolId, organizacionId);
        menuItemService.assignMenuItemToRole(rolId, menuItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{menuItemId}/revokeFromRole/{rolId}")
    // Revocar permiso de rol: Requiere permiso de revocación y que el usuario esté en la organización del rol.
    @PreAuthorize("hasAuthority('MENU_ITEM_REVOKE') and @securityUtils.isUserInRoleOrganization(#rolId, #organizacionId)")
    public ResponseEntity<Void> revokeMenuItemFromRole(@PathVariable Long organizacionId, @PathVariable Long menuItemId, @PathVariable Long rolId) {
        log.info("Revocando MenuItem ID: {} de Rol ID: {} en organización ID: {}", menuItemId, rolId, organizacionId);
        menuItemService.revokeMenuItemFromRole(rolId, menuItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-role/{rolId}")
    // Listar MenuItems por rol: Requiere permiso de lista por rol y que el usuario esté en la organización del rol.
    @PreAuthorize("hasAuthority('MENU_ITEM_LIST_BY_ROLE') and @securityUtils.isUserInRoleOrganization(#rolId, #organizacionId)")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByRolId(@PathVariable Long organizacionId, @PathVariable Long rolId) {
        log.info("Solicitud para obtener ítems de menú asignados a rol ID: {} en organización ID: {}", rolId, organizacionId);
        List<MenuItemResponse> responses = menuItemService.getMenuItemsByRolId(rolId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}