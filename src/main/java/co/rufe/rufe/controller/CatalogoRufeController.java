package co.rufe.rufe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import co.rufe.rufe.dto.catalogo.CatalogoItemRequest;
import co.rufe.rufe.dto.catalogo.CatalogoItemResponse;
import co.rufe.rufe.dto.catalogo.CatalogoMunicipioResponse;
import co.rufe.rufe.service.ICatalogoRufeService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/catalogos") // Ruta base para todos los catálogos
@Slf4j
public class CatalogoRufeController {

    private final ICatalogoRufeService catalogoRufeService;
    private final co.rufe.rufe.security.SecurityUtils securityUtils;

    public CatalogoRufeController(ICatalogoRufeService catalogoRufeService,
            co.rufe.rufe.security.SecurityUtils securityUtils) {
        this.catalogoRufeService = catalogoRufeService;
        this.securityUtils = securityUtils;
    }

    // --- Endpoints para CRUD genérico ---

    @PostMapping("/{catalogo}")
    @PreAuthorize("@securityUtils.isGlobalAdmin()")
    public ResponseEntity<Void> createItem(@PathVariable String catalogo, @RequestBody CatalogoItemRequest request) {
        log.info("Solicitud para crear item en catálogo {}: {}", catalogo, request.getNombre());
        catalogoRufeService.createItem(catalogo, request.getNombre());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{catalogo}/{id}")
    @PreAuthorize("@securityUtils.isGlobalAdmin()")
    public ResponseEntity<Void> updateItem(@PathVariable String catalogo, @PathVariable Integer id,
            @RequestBody CatalogoItemRequest request) {
        log.info("Solicitud para actualizar item en catálogo {} con ID {}: {}", catalogo, id, request.getNombre());
        catalogoRufeService.updateItem(catalogo, id, request.getNombre());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{catalogo}/{id}")
    @PreAuthorize("@securityUtils.isGlobalAdmin()")
    public ResponseEntity<Void> deleteItem(@PathVariable String catalogo, @PathVariable Integer id) {
        log.info("Solicitud para eliminar item en catálogo {} con ID {}", catalogo, id);
        catalogoRufeService.deleteItem(catalogo, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoint para TipoUbicacionBien
    @GetMapping("/tipo-ubicacion-bien")
    @PreAuthorize("isAuthenticated()") // Solo requiere que el usuario esté logueado
    public ResponseEntity<List<CatalogoItemResponse>> getAllTipoUbicacionBien() {
        log.info("Solicitud para obtener todos los TipoUbicacionBien");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllTipoUbicacionBien();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tipo-ubicacion-bien/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getTipoUbicacionBienById(@PathVariable Integer id) {
        log.info("Solicitud para obtener TipoUbicacionBien con ID: {}", id);
        return catalogoRufeService.getTipoUbicacionBienById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "TipoUbicacionBien no encontrado con ID: " + id));
    }

    // Endpoint para TipoAlojamientoActual
    @GetMapping("/tipo-alojamiento-actual")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllTipoAlojamientoActual() {
        log.info("Solicitud para obtener todos los TipoAlojamientoActual");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllTipoAlojamientoActual();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tipo-alojamiento-actual/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getTipoAlojamientoActualById(@PathVariable Integer id) {
        log.info("Solicitud para obtener TipoAlojamientoActual con ID: {}", id);
        return catalogoRufeService.getTipoAlojamientoActualById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "TipoAlojamientoActual no encontrado con ID: " + id));
    }

    // Endpoint para FormaTenenciaBien
    @GetMapping("/forma-tenencia-bien")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllFormaTenenciaBien() {
        log.info("Solicitud para obtener todos los FormaTenenciaBien");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllFormaTenenciaBien();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/forma-tenencia-bien/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getFormaTenenciaBienById(@PathVariable Integer id) {
        log.info("Solicitud para obtener FormaTenenciaBien con ID: {}", id);
        return catalogoRufeService.getFormaTenenciaBienById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "FormaTenenciaBien no encontrado con ID: " + id));
    }

    // Endpoint para EstadoBien
    @GetMapping("/estado-bien")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllEstadoBien() {
        log.info("Solicitud para obtener todos los EstadoBien");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllEstadoBien();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/estado-bien/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getEstadoBienById(@PathVariable Integer id) {
        log.info("Solicitud para obtener EstadoBien con ID: {}", id);
        return catalogoRufeService.getEstadoBienById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "EstadoBien no encontrado con ID: " + id));
    }

    // Endpoint para TipoBien
    @GetMapping("/tipo-bien")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllTipoBien() {
        log.info("Solicitud para obtener todos los TipoBien");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllTipoBien();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tipo-bien/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getTipoBienById(@PathVariable Integer id) {
        log.info("Solicitud para obtener TipoBien con ID: {}", id);
        return catalogoRufeService.getTipoBienById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "TipoBien no encontrado con ID: " + id));
    }

    // Endpoint para TipoDocumento
    @GetMapping("/tipo-documento")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllTipoDocumento() {
        log.info("Solicitud para obtener todos los TipoDocumento");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllTipoDocumento();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tipo-documento/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getTipoDocumentoById(@PathVariable Integer id) {
        log.info("Solicitud para obtener TipoDocumento con ID: {}", id);
        return catalogoRufeService.getTipoDocumentoById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "TipoDocumento no encontrado con ID: " + id));
    }

    // Endpoint para Parentesco
    @GetMapping("/parentesco")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllParentesco() {
        log.info("Solicitud para obtener todos los Parentesco");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllParentesco();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/parentesco/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getParentescoById(@PathVariable Integer id) {
        log.info("Solicitud para obtener Parentesco con ID: {}", id);
        return catalogoRufeService.getParentescoById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parentesco no encontrado con ID: " + id));
    }

    // Endpoint para Genero
    @GetMapping("/genero")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllGenero() {
        log.info("Solicitud para obtener todos los Genero");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllGenero();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/genero/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getGeneroById(@PathVariable Integer id) {
        log.info("Solicitud para obtener Genero con ID: {}", id);
        return catalogoRufeService.getGeneroById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genero no encontrado con ID: " + id));
    }

    // Endpoint para PertenenciaEtnica
    @GetMapping("/pertenencia-etnica")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllPertenenciaEtnica() {
        log.info("Solicitud para obtener todos los PertenenciaEtnica");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllPertenenciaEtnica();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/pertenencia-etnica/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CatalogoItemResponse> getPertenenciaEtnicaById(@PathVariable Integer id) {
        log.info("Solicitud para obtener PertenenciaEtnica con ID: {}", id);
        return catalogoRufeService.getPertenenciaEtnicaById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "PertenenciaEtnica no encontrado con ID: " + id));
    }

    // Nuevos endpoints para departamentos, municipios y eventos
    @GetMapping("/departamentos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllDepartamentos() {
        log.info("Solicitud para obtener todos los departamentos");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllDepartamentos();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/municipios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoMunicipioResponse>> getAllMunicipios() {
        log.info("Solicitud para obtener todos los municipios");
        List<CatalogoMunicipioResponse> response = catalogoRufeService.getAllMunicipios();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/eventos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CatalogoItemResponse>> getAllEventos() {
        log.info("Solicitud para obtener todos los eventos");
        List<CatalogoItemResponse> response = catalogoRufeService.getAllEventos();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
