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
        private final co.rufe.rufe.security.SecurityUtils securityUtils;

        public UsuarioController(IUsuarioService usuarioService, co.rufe.rufe.security.SecurityUtils securityUtils) {
                this.usuarioService = usuarioService;
                this.securityUtils = securityUtils;
        }

        @PostMapping
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<UsuarioResponse> createUsuario(
                        @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
                        @Valid @RequestBody UsuarioRequest request) {
                boolean isGlobalAdmin = securityUtils.isGlobalAdmin();
                Long targetOrgId = (isGlobalAdmin && request.getOrganizacionId() != null)
                                ? request.getOrganizacionId()
                                : userDetails.getOrganizacionId();
                log.info("Solicitud para crear usuario '{}' en organización ID: {} (isGlobalAdmin={})", request.getEmail(),
                                targetOrgId, isGlobalAdmin);
                UsuarioResponse response = usuarioService.createUsuario(targetOrgId, request, isGlobalAdmin);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @GetMapping("/{usuarioId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<UsuarioResponse> getUsuarioById(
                        @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
                        @PathVariable Long usuarioId) {
                log.info("Solicitud para obtener usuario con ID {}", usuarioId);
                boolean isAdmin = securityUtils.isGlobalAdmin();
                UsuarioResponse response = usuarioService.getUsuarioById(usuarioId, userDetails.getOrganizacionId(),
                                isAdmin);
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
                boolean isAdmin = securityUtils.isGlobalAdmin();
                List<UsuarioResponse> responses = usuarioService.getUsuariosByOrganizacionId(
                                userDetails.getOrganizacionId(),
                                isAdmin);
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
                boolean isAdmin = securityUtils.isGlobalAdmin();
                UsuarioResponse response = usuarioService.updateUsuario(usuarioId, userDetails.getOrganizacionId(),
                                request,
                                isAdmin);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @DeleteMapping("/{usuarioId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Void> deleteUsuario(
                        @AuthenticationPrincipal co.rufe.rufe.security.CustomUserDetails userDetails,
                        @PathVariable Long usuarioId) {
                log.info("Solicitud para eliminar usuario con ID {} en organización ID: {}", usuarioId,
                                userDetails.getOrganizacionId());
                boolean isAdmin = securityUtils.isGlobalAdmin();
                usuarioService.deleteUsuario(usuarioId, userDetails.getOrganizacionId(), isAdmin);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}