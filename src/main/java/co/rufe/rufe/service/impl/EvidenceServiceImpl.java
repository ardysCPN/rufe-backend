package co.rufe.rufe.service.impl;

import co.rufe.rufe.dto.evidence.EvidenceUploadDto;
import co.rufe.rufe.service.IEvidenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class EvidenceServiceImpl implements IEvidenceService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    @Value("${app.storage.local-dir:}")
    private String configuredBaseDir;

    private String getBaseDir() {
        if (configuredBaseDir != null && !configuredBaseDir.isBlank()) {
            return configuredBaseDir;
        }
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        return isWindows ? "c:/rufe/evidences" : "/app/uploads";
    }

    private String sanitizeSubFolder(String subFolder) {
        if (subFolder == null || subFolder.isBlank()) {
            return "censos";
        }
        // Sanitizar contra directory traversal
        String sanitized = subFolder.replaceAll("[^a-zA-Z0-9_-]", "");
        return sanitized.isBlank() ? "censos" : sanitized;
    }

    @Override
    public String uploadEvidenceBase64(EvidenceUploadDto dto) throws IOException {
        if (dto == null || dto.getImageBase64() == null || dto.getImageBase64().isBlank()) {
            throw new IllegalArgumentException("La cadena Base64 de la imagen no puede estar vacía.");
        }

        String rawBase64 = dto.getImageBase64().trim();
        String detectedContentType = dto.getContentType();

        // Extraer metadata si viene con formato data:image/...;base64,
        if (rawBase64.startsWith("data:")) {
            int commaIndex = rawBase64.indexOf(',');
            if (commaIndex != -1) {
                String meta = rawBase64.substring(5, commaIndex); // e.g. "image/jpeg;base64"
                if (meta.contains(";")) {
                    detectedContentType = meta.split(";")[0].trim().toLowerCase();
                }
                rawBase64 = rawBase64.substring(commaIndex + 1);
            }
        }

        // Limpiar espacios en blanco o saltos de línea del payload base64
        rawBase64 = rawBase64.replaceAll("\\s+", "");

        // Validar tipo de contenido
        if (detectedContentType == null || detectedContentType.isBlank()) {
            detectedContentType = "image/jpeg";
        } else {
            detectedContentType = detectedContentType.toLowerCase();
        }

        if (!ALLOWED_IMAGE_TYPES.contains(detectedContentType)) {
            throw new IllegalArgumentException("Tipo de imagen no permitido: " + detectedContentType + ". Permitidos: " + ALLOWED_IMAGE_TYPES);
        }

        String extension = switch (detectedContentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };

        // Decodificar Base64 a bytes físicos
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(rawBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error al decodificar la imagen en Base64: " + e.getMessage(), e);
        }

        if (imageBytes.length == 0) {
            throw new IllegalArgumentException("El contenido de la imagen decodificada está vacío.");
        }

        // Generar nombre UUID seguro en el servidor (nunca confiar en el fileName del cliente)
        String newFilename = UUID.randomUUID().toString() + extension;
        String safeSubFolder = sanitizeSubFolder(dto.getSubFolder());

        Path targetLocation = Paths.get(getBaseDir()).resolve(safeSubFolder).normalize();
        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }

        Path targetFile = targetLocation.resolve(newFilename);
        Files.write(targetFile, imageBytes);

        log.info("Evidencia Base64 guardada exitosamente: {} ({} bytes)", targetFile.toAbsolutePath(), imageBytes.length);
        return newFilename;
    }

    @Override
    public String uploadEvidence(MultipartFile file, String subFolder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        } else {
            String ct = file.getContentType();
            if (ct != null) {
                if (ct.contains("png")) {
                    extension = ".png";
                } else if (ct.contains("webp")) {
                    extension = ".webp";
                } else {
                    extension = ".jpg";
                }
            } else {
                extension = ".jpg";
            }
        }

        String newFilename = UUID.randomUUID().toString() + extension;
        String safeSubFolder = sanitizeSubFolder(subFolder);

        Path targetLocation = Paths.get(getBaseDir()).resolve(safeSubFolder).normalize();
        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }

        Path targetFile = targetLocation.resolve(newFilename);
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        log.info("Archivo local Multipart guardado exitosamente en: {}", targetFile.toAbsolutePath());

        return newFilename;
    }

    @Override
    public String getEvidenceUrl(String subFolder, String filename) {
        String folder = sanitizeSubFolder(subFolder);
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/public/evidencias/")
                .path(folder)
                .path("/")
                .path(filename)
                .toUriString();
    }
}
