package co.rufe.rufe.controller;

import co.rufe.rufe.dto.usuario.UsuarioRequest;
import co.rufe.rufe.dto.usuario.UsuarioResponse;
import co.rufe.rufe.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Slf4j
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> createUsuario(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @Valid @RequestBody UsuarioRequest request) {
        log.info("Solicitud para crear usuario '{}' en organización ID: {}", request.getEmail(),
                userDetails.getOrganizacionId());
        UsuarioResponse response = usuarioService.createUsuario(userDetails.getOrganizacionId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> getUsuarioById(
            @PathVariable Long usuarioId) {
        log.info("Solicitud para obtener usuario con ID {}", usuarioId);
        // Nota: El servicio debería validar que el usuario pertenezca a la misma
        // organización del solicitante
        // si se requiere aislamiento estricto. Por ahora pasamos solo el ID.
        UsuarioResponse response = usuarioService.getUsuarioById(usuarioId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> getUsuarioByEmail(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @PathVariable String email) {
        log.info("Solicitud para obtener usuario con email '{}' en organización ID: {}", email,
                userDetails.getOrganizacionId());
        UsuarioResponse response = usuarioService.getUsuarioByEmail(userDetails.getOrganizacionId(), email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UsuarioResponse>> getUsuariosByOrganizacionId(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails) {
        log.info("Solicitud para obtener usuarios de organización ID: {}", userDetails.getOrganizacionId());
        List<UsuarioResponse> responses = usuarioService.getUsuariosByOrganizacionId(userDetails.getOrganizacionId());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @PathVariable Long usuarioId,
            @Valid @RequestBody UsuarioRequest request) {
        log.info("Solicitud para actualizar usuario con ID {} en organización ID: {}", usuarioId,
                userDetails.getOrganizacionId());
        UsuarioResponse response = usuarioService.updateUsuario(usuarioId, userDetails.getOrganizacionId(), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUsuario(
            @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
            @PathVariable Long usuarioId) {
        log.info("Solicitud para eliminar usuario con ID {} en organización ID: {}", usuarioId,
                userDetails.getOrganizacionId());
        usuarioService.deleteUsuario(usuarioId, userDetails.getOrganizacionId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}