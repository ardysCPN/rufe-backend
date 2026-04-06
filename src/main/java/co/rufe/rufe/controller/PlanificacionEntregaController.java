package co.rufe.rufe.controller;

import co.rufe.rufe.dto.PlanificacionRequest;
import co.rufe.rufe.model.PlanificacionEntrega;
import co.rufe.rufe.service.PlanificacionEntregaService;
import co.rufe.rufe.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planificacion")
@RequiredArgsConstructor
public class PlanificacionEntregaController {

    private final PlanificacionEntregaService planificacionService;

    @PostMapping
    public ResponseEntity<PlanificacionEntrega> crearPlanificacion(@RequestBody PlanificacionRequest request) {
        Long orgId = TenantContext.getCurrentOrganizationId();
        return ResponseEntity.ok(planificacionService.planificar(orgId, request));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<PlanificacionEntrega>> getPlanificacionEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(planificacionService.obtenerPlanificacionEvento(eventoId));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<PlanificacionEntrega>> getPendientes() {
        Long orgId = TenantContext.getCurrentOrganizationId();
        return ResponseEntity.ok(planificacionService.obtenerPendientes(orgId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        planificacionService.eliminarPlanificacion(id);
        return ResponseEntity.noContent().build();
    }
}
