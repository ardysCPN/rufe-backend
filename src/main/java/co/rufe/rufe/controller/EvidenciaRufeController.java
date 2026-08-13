package co.rufe.rufe.controller;

import co.rufe.rufe.dao.IEvidenciaRufeDao;
import co.rufe.rufe.dao.IRegistroRufeDao;
import co.rufe.rufe.model.EvidenciaRufe;
import co.rufe.rufe.model.RegistroRufe;
import co.rufe.rufe.security.CustomUserDetails;
import co.rufe.rufe.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rufe/evidencias")
@RequiredArgsConstructor
@Tag(name = "Evidencias Pivote RUFE", description = "Gestión de fotos de soporte subidas al censo RUFE de manera asíncrona.")
public class EvidenciaRufeController {

    private final IEvidenciaRufeDao evidenciaRufeDao;
    private final IRegistroRufeDao registroRufeDao;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Vincular URL de foto ya subida a un registro RUFE")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> vincularEvidencia(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Object> payload) {
        Long registroRufeId = Long.valueOf(payload.get("registroRufeId").toString());
        String tipoEvidencia = (String) payload.getOrDefault("tipoEvidencia", "FOTO_CENSO");
        String fotoUrl = (String) payload.get("fotoUrl");

        Optional<RegistroRufe> rufeOpt = registroRufeDao.findById(registroRufeId);
        if (rufeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistroRufe rufe = rufeOpt.get();
        if (!securityUtils.isGlobalAdmin() && !rufe.getOrganizacionId().equals(userDetails.getOrganizacionId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No autorizado para este registro RUFE"));
        }

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
    public ResponseEntity<?> getEvidencias(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long registroRufeId) {
        Optional<RegistroRufe> rufeOpt = registroRufeDao.findById(registroRufeId);
        if (rufeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RegistroRufe rufe = rufeOpt.get();
        if (!securityUtils.isGlobalAdmin() && !rufe.getOrganizacionId().equals(userDetails.getOrganizacionId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No tiene acceso a evidencias de otra organización"));
        }

        List<EvidenciaRufe> evidencias = evidenciaRufeDao.findByRegistroRufeId(registroRufeId);
        return ResponseEntity.ok(evidencias);
    }

    @Operation(summary = "Desvincular o eliminar evidencia")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_GLOBAL')")
    public ResponseEntity<Void> eliminarEvidencia(@PathVariable Long id) {
        evidenciaRufeDao.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
