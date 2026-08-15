package co.rufe.rufe.controller;

import co.rufe.rufe.dto.evidence.EvidenceUploadDto;
import co.rufe.rufe.service.IEvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Evidencias", description = "Carga de evidencias fotográficas (JSON Base64 y Multipart).")
public class EvidenceController {

    private final IEvidenceService evidenceService;

    @Operation(summary = "Subir evidencia fotográfica en formato JSON con Base64")
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadEvidenceJson(@Valid @RequestBody EvidenceUploadDto dto) {
        log.info("Solicitud de subida Base64 recibida. fileName: '{}', contentType: '{}', subFolder: '{}'",
                dto.getFileName(), dto.getContentType(), dto.getSubFolder());

        try {
            String filename = evidenceService.uploadEvidenceBase64(dto);
            String url = evidenceService.getEvidenceUrl(dto.getSubFolder(), filename);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "filename", filename,
                    "url", url));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en evidencia Base64: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error guardando evidencia Base64 en disco: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno guardando la evidencia: " + e.getMessage()));
        }
    }

    @Operation(summary = "Subir evidencia fotográfica como Multipart/form-data (compatibilidad)")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadEvidenceMultipart(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subFolder", required = false, defaultValue = "censos") String subFolder) {

        log.info("Solicitud de subida Multipart recibida. Archivo: '{}', Tamaño: {} bytes, subFolder: '{}'",
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