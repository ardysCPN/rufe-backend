package co.rufe.rufe.controller;

import co.rufe.rufe.dto.menu.MenuDTO;
import co.rufe.rufe.service.IMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@Slf4j
public class MenuItemController {

        private final IMenuService menuService;

        public MenuItemController(IMenuService menuService) {
                this.menuService = menuService;
        }

        @GetMapping("/rol/{rolId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<List<MenuDTO>> getMenuByRol(@PathVariable Long rolId) {
                log.info("Solicitud de menú para rol ID: {}", rolId);
                return ResponseEntity.ok(menuService.getMenuByRolId(rolId));
        }
}