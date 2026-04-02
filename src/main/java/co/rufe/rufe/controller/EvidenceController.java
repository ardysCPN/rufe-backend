package co.rufe.rufe.controller;

import co.rufe.rufe.service.IEvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/evidences")
@RequiredArgsConstructor
@Tag(name = "Evidencias", description = "Generación de URLs seguras para carga multimedia en S3/MinIO.")
public class EvidenceController {

    private final IEvidenceService evidenceService;

    @Value("${aws.s3.bucket-name}")
    private String defaultBucket;

    @Operation(summary = "Generar URL Presigned para subida directa de fotos (Upload)")
    @PostMapping("/upload-url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getPresignedUploadUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {
        
        String extension = "";
        if (filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf("."));
        }
        
        String keyName = "censos/" + UUID.randomUUID() + extension;
        String url = evidenceService.generatePresignedUploadUrl(defaultBucket, keyName, contentType);
        
        return ResponseEntity.ok(Map.of(
                "url", url,
                "key", keyName
        ));
    }

    @Operation(summary = "Generar URL Presigned para vista de fotos (Download)")
    @GetMapping("/download-url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getPresignedDownloadUrl(@RequestParam String key) {
        String url = evidenceService.generatePresignedDownloadUrl(defaultBucket, key);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
