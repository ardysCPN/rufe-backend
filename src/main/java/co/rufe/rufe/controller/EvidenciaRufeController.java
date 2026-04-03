package co.rufe.rufe.controller;

import co.rufe.rufe.dao.IEvidenciaRufeDao;
import co.rufe.rufe.model.EvidenciaRufe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rufe/evidencias")
@RequiredArgsConstructor
@Tag(name = "Evidencias Pivote RUFE", description = "Gestión de fotos de soporte subidas al censo RUFE de manera asíncrona.")
public class EvidenciaRufeController {

    private final IEvidenciaRufeDao evidenciaRufeDao;

    @Operation(summary = "Vincular URL de foto ya subida a un registro RUFE")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EvidenciaRufe> vincularEvidencia(@RequestBody Map<String, Object> payload) {
        Long registroRufeId = Long.valueOf(payload.get("registroRufeId").toString());
        String tipoEvidencia = (String) payload.getOrDefault("tipoEvidencia", "FOTO_CENSO");
        String fotoUrl = (String) payload.get("fotoUrl");

        EvidenciaRufe guardada = evidenciaRufeDao.save(EvidenciaRufe.builder()
                .registroRufeId(registroRufeId)
                .tipoEvidencia(tipoEvidencia)
                .fotoUrl(fotoUrl)
                .fechaCarga(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(guardada);
    }

    @Operation(summary = "Obtener todas las evidencias atadas a un RUFE")
    @GetMapping("/{registroRufeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EvidenciaRufe>> getEvidencias(@PathVariable Long registroRufeId) {
        return ResponseEntity.ok(evidenciaRufeDao.findByRegistroRufeId(registroRufeId));
    }

    @Operation(summary = "Desvincular o eliminar evidencia")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<Void> eliminarEvidencia(@PathVariable Long id) {
        evidenciaRufeDao.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
