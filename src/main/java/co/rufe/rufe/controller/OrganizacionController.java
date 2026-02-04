package co.rufe.rufe.controller;

import co.rufe.rufe.dto.organizacion.OrganizacionRequest;
import co.rufe.rufe.dto.organizacion.OrganizacionResponse;
import co.rufe.rufe.service.IOrganizacionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones")
@Slf4j
public class OrganizacionController {

    private final IOrganizacionService organizacionService;

    public OrganizacionController(IOrganizacionService organizacionService) {
        this.organizacionService = organizacionService;
    }

    @PostMapping
    // Solo un ADMIN_GLOBAL puede crear nuevas organizaciones.
    // Permiso requerido: organizaciones:crear
    // Solo un ADMIN_GLOBAL puede crear nuevas organizaciones.
    // Permiso requerido: organizaciones:crear
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizacionResponse> createOrganizacion(@Valid @RequestBody OrganizacionRequest request) {
        log.info("Solicitud para crear una nueva organización: {}", request.getNombreOrganizacion());
        OrganizacionResponse response = organizacionService.createOrganizacion(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{organizacionId}")
    // Permite a un ADMIN_GLOBAL leer cualquier organización, o a un usuario de esa
    // organización leer la suya.
    // Permiso requerido: organizaciones:leer
    // Permite a un ADMIN_GLOBAL leer cualquier organización, o a un usuario de esa
    // organización leer la suya.
    // Permiso requerido: organizaciones:leer
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizacionResponse> getOrganizacionById(@PathVariable Long organizacionId) {
        log.info("Solicitud para obtener organización con ID: {}", organizacionId);
        OrganizacionResponse response = organizacionService.getOrganizacionById(organizacionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/nombre/{nombreOrganizacion}")
    // Permite a un ADMIN_GLOBAL leer cualquier organización por nombre, o a un
    // usuario de esa organización leer la suya.
    // Permiso requerido: organizaciones:leer
    // Permite a un ADMIN_GLOBAL leer cualquier organización por nombre, o a un
    // usuario de esa organización leer la suya.
    // Permiso requerido: organizaciones:leer
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizacionResponse> getOrganizacionByNombre(@PathVariable String nombreOrganizacion) {
        log.info("Solicitud para obtener organización con nombre: {}", nombreOrganizacion);
        OrganizacionResponse response = organizacionService.getOrganizacionByNombre(nombreOrganizacion);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    // Solo un ADMIN_GLOBAL debería poder listar TODAS las organizaciones.
    // Permiso requerido: organizaciones:listar
    // Solo un ADMIN_GLOBAL debería poder listar TODAS las organizaciones.
    // Permiso requerido: organizaciones:listar
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrganizacionResponse>> getAllOrganizaciones() {
        log.info("Solicitud para obtener todas las organizaciones.");
        List<OrganizacionResponse> responses = organizacionService.getAllOrganizaciones();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{organizacionId}")
    // Permite a un ADMIN_GLOBAL actualizar cualquier organización, o a un usuario
    // de esa organización actualizar la suya.
    // Permiso requerido: organizaciones:actualizar
    // Permite a un ADMIN_GLOBAL actualizar cualquier organización, o a un usuario
    // de esa organización actualizar la suya.
    // Permiso requerido: organizaciones:actualizar
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizacionResponse> updateOrganizacion(@PathVariable Long organizacionId,
            @Valid @RequestBody OrganizacionRequest request) {
        log.info("Solicitud para actualizar organización con ID: {}", organizacionId);
        OrganizacionResponse response = organizacionService.updateOrganizacion(organizacionId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{organizacionId}")
    // Solo un ADMIN_GLOBAL puede eliminar organizaciones.
    // Permiso requerido: organizaciones:eliminar
    // Solo un ADMIN_GLOBAL puede eliminar organizaciones.
    // Permiso requerido: organizaciones:eliminar
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteOrganizacion(@PathVariable Long organizacionId) {
        log.info("Solicitud para eliminar organización con ID: {}", organizacionId);
        organizacionService.deleteOrganizacion(organizacionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}