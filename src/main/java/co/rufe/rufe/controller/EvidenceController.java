package co.rufe.rufe.controller;

import co.rufe.rufe.service.IEvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/evidences")
@RequiredArgsConstructor
@Tag(name = "Evidencias", description = "Carga multimedia de censos a disco local.")
public class EvidenceController {

    private final IEvidenceService evidenceService;

    @Operation(summary = "Subir evidencia fotográfica")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.ALL_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadEvidence(
            @RequestParam(value = "file", required = false) MultipartFile fileParam,
            @RequestPart(value = "file", required = false) MultipartFile filePart,
            @RequestParam(value = "subFolder", defaultValue = "censos") String subFolder,
            HttpServletRequest request) {

        MultipartFile fileToUpload = fileParam != null ? fileParam : filePart;

        // Fallback: Si no se mapeó automáticamente por proxy o nombre de parte
        if (fileToUpload == null && request instanceof MultipartHttpServletRequest multipartRequest) {
            fileToUpload = multipartRequest.getFile("file");
            if (fileToUpload == null && !multipartRequest.getFileMap().isEmpty()) {
                fileToUpload = multipartRequest.getFileMap().values().iterator().next();
            }
        }

        if (fileToUpload == null || fileToUpload.isEmpty()) {
            log.warn("Solicitud de subida de evidencia recibida sin archivo válido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se encontró el archivo 'file' en la petición multipart."));
        }

        try {
            log.info("Subiendo archivo de evidencia: {}, tamaño: {} bytes, subFolder: {}",
                    fileToUpload.getOriginalFilename(), fileToUpload.getSize(), subFolder);

            String filename = evidenceService.uploadEvidence(fileToUpload, subFolder);
            String url = evidenceService.getEvidenceUrl(subFolder, filename);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "filename", filename,
                    "url", url
            ));
        } catch (Exception e) {
            log.error("Error guardando el archivo de evidencia: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error guardando el archivo: " + e.getMessage()));
        }
    }
}
