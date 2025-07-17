package co.rufe.rufe.controller;

import co.rufe.rufe.dto.rol.RolRequest;
import co.rufe.rufe.dto.rol.RolResponse;
import co.rufe.rufe.service.IRolService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones/{organizacionId}/roles")
@Slf4j
public class RolController {

    private final IRolService rolService;

    public RolController(IRolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    // Ahora requiere el permiso 'ROL_CREATE'
    @PreAuthorize("hasAuthority('ROL_CREATE') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<RolResponse> createRol(@PathVariable Long organizacionId, @Valid @RequestBody RolRequest request) {
        log.info("Solicitud para crear rol '{}' en organización ID: {}", request.getNombreRol(), organizacionId);
        RolResponse response = rolService.createRol(organizacionId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{rolId}")
    // Requiere el permiso 'ROL_READ'
    @PreAuthorize("hasAuthority('ROL_READ') and @securityUtils.isUserInRoleOrganization(#rolId, #organizacionId)")
    public ResponseEntity<RolResponse> getRolById(@PathVariable Long organizacionId, @PathVariable Long rolId) {
        log.info("Solicitud para obtener rol con ID {} en organización ID: {}", rolId, organizacionId);
        RolResponse response = rolService.getRolById(rolId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
 
    @GetMapping("/nombre/{nombreRol}")
    // Requiere el permiso 'ROL_READ'
    @PreAuthorize("hasAuthority('ROL_READ') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<RolResponse> getRolByNombre(@PathVariable Long organizacionId, @PathVariable String nombreRol) {
        log.info("Solicitud para obtener rol '{}' en organización ID: {}", nombreRol, organizacionId);
        RolResponse response = rolService.getRolByNombre(organizacionId, nombreRol);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    // Requiere el permiso 'ROL_LIST'
    @PreAuthorize("hasAuthority('ROL_LIST') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<RolResponse>> getRolesByOrganizacionId(@PathVariable Long organizacionId) {
        log.info("Solicitud para obtener roles de organización ID: {}", organizacionId);
        List<RolResponse> responses = rolService.getRolesByOrganizacionId(organizacionId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{rolId}")
    // Requiere el permiso 'ROL_UPDATE'
    @PreAuthorize("hasAuthority('ROL_UPDATE') and @securityUtils.isUserInRoleOrganization(#rolId, #organizacionId)")
    public ResponseEntity<RolResponse> updateRol(@PathVariable Long organizacionId, @PathVariable Long rolId, @Valid @RequestBody RolRequest request) {
        log.info("Solicitud para actualizar rol con ID {} en organización ID: {}", rolId, organizacionId);
        RolResponse response = rolService.updateRol(rolId, organizacionId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{rolId}")
    // Requiere el permiso 'ROL_DELETE'
    @PreAuthorize("hasAuthority('ROL_DELETE') and @securityUtils.isUserInRoleOrganization(#rolId, #organizacionId)")
    public ResponseEntity<Void> deleteRol(@PathVariable Long organizacionId, @PathVariable Long rolId) {
        log.info("Solicitud para eliminar rol con ID {} en organización ID: {}", rolId, organizacionId);
        rolService.deleteRol(rolId, organizacionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}