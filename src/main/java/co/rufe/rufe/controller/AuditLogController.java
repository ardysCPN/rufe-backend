package co.rufe.rufe.controller;

import co.rufe.rufe.model.AuditLog;
import co.rufe.rufe.service.IAuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Slf4j
public class AuditLogController {

    private final IAuditLogService auditLogService;
    private final co.rufe.rufe.security.SecurityUtils securityUtils;

    public AuditLogController(IAuditLogService auditLogService, co.rufe.rufe.security.SecurityUtils securityUtils) {
        this.auditLogService = auditLogService;
        this.securityUtils = securityUtils;
    }

    /**
     * Obtiene los logs de auditoría.
     * ADMIN_GLOBAL: obtiene todos los logs de todas las organizaciones.
     * Usuario normal con permiso: obtiene únicamente los logs de su organización.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AuditLog>> getLogs(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails) {
        boolean isAdmin = securityUtils.isGlobalAdmin();
        if (isAdmin) {
            log.info("ADMIN_GLOBAL solicitando todos los logs de auditoría.");
            return ResponseEntity.ok(auditLogService.getAllLogs());
        } else {
            log.info("Solicitud de auditoría para organización ID: {}", userDetails.getOrganizacionId());
            return ResponseEntity.ok(auditLogService.getLogsByOrganizacion(userDetails.getOrganizacionId()));
        }
    }
}
