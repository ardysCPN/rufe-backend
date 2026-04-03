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

    @Value("${app.storage.local-dir:c:/rufe/evidences}") // Folder of storage
    private String baseDir;

    @Override
    public String uploadEvidence(MultipartFile file, String subFolder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFilename = UUID.randomUUID().toString() + extension;
        Path targetLocation = Paths.get(baseDir).resolve(subFolder).normalize();
        
        if(!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }
        Path targetFile = targetLocation.resolve(newFilename);
        
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        log.info("Archivo local guardado en: {}", targetFile.toAbsolutePath());
        
        return newFilename;
    }

    @Override
    public String getEvidenceUrl(String subFolder, String filename) {
        // Retorna la URL pública a la que Angular accederá para descargar/ver la imagen
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/public/evidencias/")
                .path(subFolder)
                .path("/")
                .path(filename)
                .toUriString();
    }
}
