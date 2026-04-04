package co.rufe.rufe.controller;

import co.rufe.rufe.service.IEvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/evidences")
@RequiredArgsConstructor
@Tag(name = "Evidencias", description = "Carga multimedia de censos a disco local.")
public class EvidenceController {

    private final IEvidenceService evidenceService;

    @Operation(summary = "Subir evidencia fotográfica")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadEvidence(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subFolder", defaultValue = "censos") String subFolder) {
        
        try {
            String filename = evidenceService.uploadEvidence(file, subFolder);
            String url = evidenceService.getEvidenceUrl(subFolder, filename);
            
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "filename", filename,
                    "url", url
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error guardando el archivo loco: " + e.getMessage()));
        }
    }
}
