package co.rufe.rufe.controller;

import co.rufe.rufe.dto.PlanificacionRequest;
import co.rufe.rufe.model.PlanificacionEntrega;
import co.rufe.rufe.security.CustomUserDetails;
import co.rufe.rufe.security.SecurityUtils;
import co.rufe.rufe.service.PlanificacionEntregaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planificacion")
@RequiredArgsConstructor
public class PlanificacionEntregaController {

    private final PlanificacionEntregaService planificacionService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlanificacionEntrega> crearPlanificacion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PlanificacionRequest request) {
        Long orgId = userDetails.getOrganizacionId();
        return ResponseEntity.ok(planificacionService.planificar(orgId, request));
    }

    /**
     * Obtiene planificaciones de un evento.
     * Valida que el evento pertenezca a la organización del usuario (excepto ADMIN_GLOBAL).
     */
    @GetMapping("/evento/{eventoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanificacionEntrega>> getPlanificacionEvento(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long eventoId) {
        boolean isAdmin = securityUtils.isGlobalAdmin();
        Long orgId = userDetails.getOrganizacionId();
        return ResponseEntity.ok(planificacionService.obtenerPlanificacionEvento(eventoId, orgId, isAdmin));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanificacionEntrega>> getPendientes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long orgId = userDetails.getOrganizacionId();
        return ResponseEntity.ok(planificacionService.obtenerPendientes(orgId));
    }

    /**
     * Elimina una planificación validando que pertenezca a la organización del usuario.
     * El ADMIN_GLOBAL puede eliminar planificaciones de cualquier organización.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        boolean isAdmin = securityUtils.isGlobalAdmin();
        Long orgId = userDetails.getOrganizacionId();
        planificacionService.eliminarPlanificacion(id, orgId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
