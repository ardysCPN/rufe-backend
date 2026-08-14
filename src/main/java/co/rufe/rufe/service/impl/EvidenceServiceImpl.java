package co.rufe.rufe.service.impl;

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
import java.util.UUID;

@Slf4j
@Service
public class EvidenceServiceImpl implements IEvidenceService {

    @Value("${app.storage.local-dir:}")
    private String configuredBaseDir;

    private String getBaseDir() {
        if (configuredBaseDir != null && !configuredBaseDir.isBlank()) {
            return configuredBaseDir;
        }
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        return isWindows ? "c:/rufe/evidences" : "/app/uploads";
    }

    @Override
    public String uploadEvidence(MultipartFile file, String subFolder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
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
        Path targetLocation = Paths.get(getBaseDir()).resolve(subFolder != null ? subFolder : "censos").normalize();
        
        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }
        Path targetFile = targetLocation.resolve(newFilename);
        
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        log.info("Archivo local guardado exitosamente en: {}", targetFile.toAbsolutePath());
        
        return newFilename;
    }

    @Override
    public String getEvidenceUrl(String subFolder, String filename) {
        String folder = (subFolder != null && !subFolder.isBlank()) ? subFolder : "censos";
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/public/evidencias/")
                .path(folder)
                .path("/")
                .path(filename)
                .toUriString();
    }
}

