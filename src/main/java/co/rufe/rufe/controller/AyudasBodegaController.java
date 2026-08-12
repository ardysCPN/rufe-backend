package co.rufe.rufe.controller;

import co.rufe.rufe.model.AyudaCatalogo;
import co.rufe.rufe.model.AyudasEntregadas;
import co.rufe.rufe.model.BodegaInventario;
import co.rufe.rufe.security.SecurityUtils;
import co.rufe.rufe.service.ILogisticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
@Tag(name = "Bodega y Entregas", description = "Gestión de Módulo de Ayudas y Logística de Bodegas")
public class AyudasBodegaController {

    private final ILogisticaService logisticaService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Obtener el catálogo general de ayudas posibles", description = "Retorna la lista de ítems configurables (Tejas, Colchonetas, Etcetera).")
    @GetMapping("/catalogo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AyudaCatalogo>> getCatalogo() {
        Long orgId = securityUtils.getCurrentUserOrganizationId();
        return ResponseEntity.ok(logisticaService.getCatalogoAyudas(orgId));
    }

    @Operation(summary = "Agregar un nuevo tipo de ayuda al catálogo transversal", description = "Crea un nuevo ítem como 'Colchones', 'Mercados', etc.")
    @PostMapping("/catalogo")
    @PreAuthorize("@securityUtils.isGlobalAdmin()")
    public ResponseEntity<AyudaCatalogo> addCatalogoItem(@RequestBody AyudaCatalogo request) {
        Long orgId = securityUtils.getCurrentUserOrganizationId();
        return ResponseEntity.ok(logisticaService.addCatalogoAyuda(orgId, request));
    }

    @Operation(summary = "Ver inventario de bodega de mi organización", description = "Retorna el stock actual.")
    @GetMapping("/inventario")
    @PreAuthorize("hasAuthority('bodega:leer') OR hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<List<BodegaInventario>> getInventario() {
        Long orgId = securityUtils.getCurrentUserOrganizationId();
        return ResponseEntity.ok(logisticaService.getInventarioTotal(orgId));
    }

    @Operation(summary = "Ajustar stock (Entrada de Bodega)", description = "Agrega stock a la bodega. Usa cantidades negativas para mermas forzadas.")
    @PostMapping("/inventario")
    @PreAuthorize("hasAuthority('bodega:actualizar') OR hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<BodegaInventario> ajustarInventario(
            @RequestBody Map<String, Object> payload) {
        Long orgId = securityUtils.getCurrentUserOrganizationId();
        Integer ayudaId = (Integer) payload.get("ayudaCatalogoId");
        BigDecimal cantidad = new BigDecimal(payload.get("cantidad").toString());

        BodegaInventario result = logisticaService.addStockBodega(orgId, ayudaId, cantidad);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Despachar una ayuda y asociarla al RUFE activo", description = "Entrega elementos directamente desde Bodega.")
    @PostMapping("/entregas")
    @PreAuthorize("hasAuthority('bodega:actualizar') OR hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<AyudasEntregadas> realizarEntrega(
            @RequestBody Map<String, Object> payload) {
        Long orgId = securityUtils.getCurrentUserOrganizationId();
        // Parsing input
        Long registroRufeId = Long.valueOf(payload.get("registroRufeId").toString());
        Integer ayudaCatalogoId = (Integer) payload.get("ayudaCatalogoId");
        BigDecimal cantidad = new BigDecimal(payload.get("cantidad").toString());
        String firma = (String) payload.get("firmaDigital"); // Base64 or external URI
        String foto = (String) payload.get("evidenciaFotoUrl");
        
        AyudasEntregadas result = logisticaService.despacharAyuda(orgId, registroRufeId, ayudaCatalogoId, cantidad, firma, foto);
        
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "Historial de ayudas entregadas de Mi Organización")
    @GetMapping("/entregas")
    @PreAuthorize("hasAuthority('bodega:leer') OR hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<List<AyudasEntregadas>> historialEntregas() {
        Long orgId = securityUtils.getCurrentUserOrganizationId();
        return ResponseEntity.ok(logisticaService.getAyudasEntregadasPorOrganizacion(orgId));
    }
}
