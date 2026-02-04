package co.rufe.rufe.controller;

import co.rufe.rufe.dto.rol.RolRequest;
import co.rufe.rufe.dto.rol.RolResponse;
import co.rufe.rufe.service.IRolService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Slf4j
public class RolController {

    private final IRolService rolService;

    public RolController(IRolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RolResponse> createRol(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @Valid @RequestBody RolRequest request) {
        log.info("Solicitud para crear rol '{}' en organización ID: {}", request.getNombreRol(),
                userDetails.getOrganizacionId());
        RolResponse response = rolService.createRol(userDetails.getOrganizacionId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{rolId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RolResponse> getRolById(@PathVariable Long rolId) {
        log.info("Solicitud para obtener rol con ID {}", rolId);
        // Nota: Idealmente validar que el rol pertenezca a la organizacion del usuario
        // si
        // se requiere aislamiento estricto
        RolResponse response = rolService.getRolById(rolId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/nombre/{nombreRol}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RolResponse> getRolByNombre(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @PathVariable String nombreRol) {
        log.info("Solicitud para obtener rol '{}' en organización ID: {}", nombreRol, userDetails.getOrganizacionId());
        RolResponse response = rolService.getRolByNombre(userDetails.getOrganizacionId(), nombreRol);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RolResponse>> getRolesByOrganizacionId(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails) {
        log.info("Solicitud para obtener roles de organización ID: {}", userDetails.getOrganizacionId());
        List<RolResponse> responses = rolService.getRolesByOrganizacionId(userDetails.getOrganizacionId());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{rolId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RolResponse> updateRol(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @PathVariable Long rolId,
            @Valid @RequestBody RolRequest request) {
        log.info("Solicitud para actualizar rol con ID {} en organización ID: {}", rolId,
                userDetails.getOrganizacionId());
        RolResponse response = rolService.updateRol(rolId, userDetails.getOrganizacionId(), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{rolId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteRol(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @PathVariable Long rolId) {
        log.info("Solicitud para eliminar rol con ID {} en organización ID: {}", rolId,
                userDetails.getOrganizacionId());
        rolService.deleteRol(rolId, userDetails.getOrganizacionId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}