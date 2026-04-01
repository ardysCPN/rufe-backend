package co.rufe.rufe.controller;

import co.rufe.rufe.dto.rufe.RegistroRufeCreateRequest;
import co.rufe.rufe.dto.rufe.RegistroRufeResponse;
import co.rufe.rufe.security.CustomUserDetails;
import co.rufe.rufe.service.IRegistroRufeService;
import co.rufe.rufe.service.IRufeReportService;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rufe")
@Slf4j
public class RegistroRufeController {

    private final IRegistroRufeService registroRufeService;
    private final IRufeReportService reportService;
    private final co.rufe.rufe.security.SecurityUtils securityUtils;

    public RegistroRufeController(IRegistroRufeService registroRufeService, IRufeReportService reportService,
            co.rufe.rufe.security.SecurityUtils securityUtils) {
        this.registroRufeService = registroRufeService;
        this.reportService = reportService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RegistroRufeResponse> crearRegistro(
            @Valid @RequestBody RegistroRufeCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("Recibiendo registro RUFE de usuario ID: {}", userDetails.getId());

        RegistroRufeResponse response = registroRufeService.crearRegistro(
                request,
                userDetails.getId(),
                userDetails.getOrganizacionId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<java.util.List<RegistroRufeResponse>> listRegistros(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isAdmin = securityUtils.isGlobalAdmin();

        return ResponseEntity.ok(registroRufeService.listarTodos(userDetails.getOrganizacionId(), isAdmin));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RegistroRufeResponse> getRegistroById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @org.springframework.web.bind.annotation.PathVariable Long id) {

        boolean isAdmin = securityUtils.isGlobalAdmin();

        return ResponseEntity.ok(registroRufeService.obtenerPorId(id, userDetails.getOrganizacionId(), isAdmin));
    }

    @GetMapping("/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> generarPdf() throws Exception {
        // ... rest same ...

        // RegistroRufeCreateRequest dto = servicio.obtenerRufe();

        byte[] pdf = reportService.generarReporteRufe(0L);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=rufe.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
