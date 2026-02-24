package co.rufe.rufe.controller;

import co.rufe.rufe.dto.menu.MenuDTO;
import co.rufe.rufe.service.IMenuService;
import co.rufe.rufe.dao.IMenuRolesDao; // Para obtener los IDs directamente
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu")
@Slf4j
public class MenuManagementController {

    private final IMenuService menuService;
    private final IMenuRolesDao menuRolesDao;

    public MenuManagementController(IMenuService menuService, IMenuRolesDao menuRolesDao) {
        this.menuService = menuService;
        this.menuRolesDao = menuRolesDao;
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MenuDTO>> getAllMenus() {
        log.info("Solicitud para obtener todas las opciones de menú");
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/roles/{rolId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getRoleMenuIds(@PathVariable Long rolId) {
        log.info("Solicitud para obtener IDs de menú del rol: {}", rolId);
        return ResponseEntity.ok(menuRolesDao.findMenuIdsByRolId(rolId));
    }

    @PostMapping("/roles/{rolId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateRoleMenus(@PathVariable Long rolId, @RequestBody List<Long> menuIds) {
        log.info("Actualizando menú para el rol ID: {}. Total items: {}", rolId, menuIds.size());
        menuService.updateRoleMenus(rolId, menuIds);
        return ResponseEntity.noContent().build();
    }
}
