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

    @GetMapping
    @PreAuthorize("@securityUtils.isGlobalAdmin()")
    public ResponseEntity<List<AuditLog>> getLogs(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails) {
        log.info("Solicitud de auditoría para organización ID: {}", userDetails.getOrganizacionId());
        List<AuditLog> logs = auditLogService.getLogsByOrganizacion(userDetails.getOrganizacionId());
        return ResponseEntity.ok(logs);
    }
}
