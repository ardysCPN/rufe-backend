package co.rufe.rufe.controller;

import co.rufe.rufe.dto.usuario.UsuarioRequest;
import co.rufe.rufe.dto.usuario.UsuarioResponse;
import co.rufe.rufe.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones/{organizacionId}/usuarios")
@Slf4j
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    // Permite crear un usuario dentro de una organización si se tiene el permiso y se pertenece a ella.
    @PreAuthorize("hasAuthority('USUARIO_CREATE') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<UsuarioResponse> createUsuario(@PathVariable Long organizacionId, @Valid @RequestBody UsuarioRequest request) {
        log.info("Solicitud para crear usuario '{}' en organización ID: {}", request.getEmail(), organizacionId);
        UsuarioResponse response = usuarioService.createUsuario(organizacionId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{usuarioId}")
    // Permite leer un usuario específico si se tiene el permiso y el usuario pertenece a la organización
    // (validando que el usuarioId también pertenezca a esa organizacionId).
    @PreAuthorize("hasAuthority('USUARIO_READ') and @securityUtils.isUserInUserOrganization(#usuarioId, #organizacionId)")
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable Long organizacionId, @PathVariable Long usuarioId) {
        log.info("Solicitud para obtener usuario con ID {} en organización ID: {}", usuarioId, organizacionId);
        UsuarioResponse response = usuarioService.getUsuarioById(usuarioId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    // Permite leer un usuario por email si se tiene el permiso y el usuario pertenece a la organización
    // (validando que el email corresponde a un usuario de esa organizacionId).
    @PreAuthorize("hasAuthority('USUARIO_READ') and @securityUtils.isUserInOrganizationAndEmailMatches(#organizacionId, #email)")
    public ResponseEntity<UsuarioResponse> getUsuarioByEmail(@PathVariable Long organizacionId, @PathVariable String email) {
        log.info("Solicitud para obtener usuario con email '{}' en organización ID: {}", email, organizacionId);
        UsuarioResponse response = usuarioService.getUsuarioByEmail(organizacionId, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    // Permite listar usuarios de una organización si se tiene el permiso y se pertenece a ella.
    @PreAuthorize("hasAuthority('USUARIO_LIST') and @securityUtils.isUserInOrganization(#organizacionId)")
    public ResponseEntity<List<UsuarioResponse>> getUsuariosByOrganizacionId(@PathVariable Long organizacionId) {
        log.info("Solicitud para obtener usuarios de organización ID: {}", organizacionId);
        List<UsuarioResponse> responses = usuarioService.getUsuariosByOrganizacionId(organizacionId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{usuarioId}")
    // Permite actualizar un usuario específico si se tiene el permiso y el usuario pertenece a la organización.
    @PreAuthorize("hasAuthority('USUARIO_UPDATE') and @securityUtils.isUserInUserOrganization(#usuarioId, #organizacionId)")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Long organizacionId, @PathVariable Long usuarioId, @Valid @RequestBody UsuarioRequest request) {
        log.info("Solicitud para actualizar usuario con ID {} en organización ID: {}", usuarioId, organizacionId);
        UsuarioResponse response = usuarioService.updateUsuario(usuarioId, organizacionId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{usuarioId}")
    // Permite eliminar un usuario específico si se tiene el permiso y el usuario pertenece a la organización.
    @PreAuthorize("hasAuthority('USUARIO_DELETE') and @securityUtils.isUserInUserOrganization(#usuarioId, #organizacionId)")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long organizacionId, @PathVariable Long usuarioId) {
        log.info("Solicitud para eliminar usuario con ID {} en organización ID: {}", usuarioId, organizacionId);
        usuarioService.deleteUsuario(usuarioId, organizacionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}