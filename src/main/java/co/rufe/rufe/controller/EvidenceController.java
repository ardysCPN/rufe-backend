package co.rufe.rufe.controller;

import co.rufe.rufe.service.IEvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/evidences")
@RequiredArgsConstructor
@Tag(name = "Evidencias", description = "Carga multimedia de censos a disco local.")
public class EvidenceController {

    private final IEvidenceService evidenceService;

    @Operation(summary = "Subir evidencia fotográfica")
    // Simplificado consumes. Solo MULTIPART_FORM_DATA_VALUE es necesario.
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadEvidence(
            // Hacemos el archivo obligatorio. Si no llega, Spring maneja el error 400
            // automáticamente.
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subFolder", required = false, defaultValue = "censos") String subFolder) {

        log.info("Solicitud de subida recibida. Archivo: '{}', Tamaño: {} bytes, subFolder: '{}'",
                file.getOriginalFilename(), file.getSize(), subFolder);

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El archivo proporcionado está vacío."));
        }

        try {
            String filename = evidenceService.uploadEvidence(file, subFolder);
            String url = evidenceService.getEvidenceUrl(subFolder, filename);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "filename", filename,
                    "url", url));
        } catch (Exception e) {
            log.error("Error guardando el archivo de evidencia: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error guardando el archivo: " + e.getMessage()));
        }
    }
}