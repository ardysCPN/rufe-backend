package co.rufe.rufe.controller;

import co.rufe.rufe.dto.evento.EventoRealRequest;
import co.rufe.rufe.dto.evento.EventoRealResponse;
import co.rufe.rufe.security.CustomUserDetails;
import co.rufe.rufe.service.IEventoRealService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@Slf4j
public class EventoRealController {

    private final IEventoRealService eventoService;
    private final co.rufe.rufe.security.SecurityUtils securityUtils;

    public EventoRealController(IEventoRealService eventoService, co.rufe.rufe.security.SecurityUtils securityUtils) {
        this.eventoService = eventoService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventoRealResponse> createEvento(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EventoRealRequest request) {
        log.info("Creando evento real para org: {}", userDetails.getOrganizacionId());
        EventoRealResponse response = eventoService.createEvento(request, userDetails.getOrganizacionId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventoRealResponse> updateEvento(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody EventoRealRequest request) {
        log.info("Actualizando evento ID: {} para org: {}", id, userDetails.getOrganizacionId());
        EventoRealResponse response = eventoService.updateEvento(id, request, userDetails.getOrganizacionId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventoRealResponse> getEventoById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        log.info("Consultando evento ID: {} para org: {}", id, userDetails.getOrganizacionId());
        boolean isAdmin = securityUtils.isGlobalAdmin();
        EventoRealResponse response = eventoService.getEventoById(id, userDetails.getOrganizacionId(), isAdmin);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EventoRealResponse>> getAllEventos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Listando eventos para org: {}", userDetails.getOrganizacionId());
        boolean isAdmin = securityUtils.isGlobalAdmin();
        List<EventoRealResponse> response = eventoService.getAllEventos(userDetails.getOrganizacionId(), isAdmin);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteEvento(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        log.info("Eliminando (lógico) evento ID: {} para org: {}", id, userDetails.getOrganizacionId());
        eventoService.deleteEvento(id, userDetails.getOrganizacionId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
