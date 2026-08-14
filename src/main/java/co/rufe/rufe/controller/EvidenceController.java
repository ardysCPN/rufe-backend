package co.rufe.rufe.controller;

import co.rufe.rufe.service.IEvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
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
            @RequestParam(value = "subFolder", required = false, defaultValue = "censos") String subFolder,
            HttpServletRequest request) {

        MultipartFile fileToUpload = (fileParam != null && !fileParam.isEmpty()) ? fileParam : null;

        // 1. Fallback desglosando wrappers de Spring Security con WebUtils
        if (fileToUpload == null) {
            MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
            if (multipartRequest != null) {
                fileToUpload = multipartRequest.getFile("file");
                if ((fileToUpload == null || fileToUpload.isEmpty()) && !multipartRequest.getFileMap().isEmpty()) {
                    for (MultipartFile f : multipartRequest.getFileMap().values()) {
                        if (f != null && !f.isEmpty()) {
                            fileToUpload = f;
                            break;
                        }
                    }
                }
            }
        }

        // 2. Fallback mediante partes nativas de Jakarta Servlet (Tomcat / StandardServlet)
        if (fileToUpload == null || fileToUpload.isEmpty()) {
            try {
                Collection<Part> parts = request.getParts();
                if (parts != null && !parts.isEmpty()) {
                    for (Part part : parts) {
                        String submittedFileName = part.getSubmittedFileName();
                        if (submittedFileName != null && !submittedFileName.isBlank() && part.getSize() > 0) {
                            fileToUpload = new PartMultipartFileAdapter(part);
                            break;
                        }
                    }
                    if (fileToUpload == null) {
                        Part filePart = request.getPart("file");
                        if (filePart != null && filePart.getSize() > 0) {
                            fileToUpload = new PartMultipartFileAdapter(filePart);
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("No se pudieron leer parts directamente de la solicitud servlet: {}", e.getMessage());
            }
        }

        // 3. Fallback a flujo binario directo si la petición vino como imagen directa
        if (fileToUpload == null || fileToUpload.isEmpty()) {
            try {
                String contentType = request.getContentType();
                if (contentType != null && (contentType.startsWith("image/") || contentType.equals(MediaType.APPLICATION_OCTET_STREAM_VALUE))) {
                    byte[] bytes = request.getInputStream().readAllBytes();
                    if (bytes.length > 0) {
                        fileToUpload = new InMemoryMultipartFile("file", "evidencia.jpg", contentType, bytes);
                    }
                }
            } catch (Exception e) {
                log.debug("No se pudo leer stream directo de imagen: {}", e.getMessage());
            }
        }

        // Determinar subFolder si vino por parámetro o dentro del request
        if (subFolder == null || subFolder.isBlank() || subFolder.equals("censos")) {
            String paramSub = request.getParameter("subFolder");
            if (paramSub != null && !paramSub.isBlank()) {
                subFolder = paramSub;
            }
        }

        if (fileToUpload == null || fileToUpload.isEmpty()) {
            log.warn("Solicitud de subida de evidencia recibida sin archivo válido en /api/evidences/upload.");
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

    /**
     * Adaptador para envolver un Part nativo de Jakarta Servlet como MultipartFile de Spring.
     */
    private static class PartMultipartFileAdapter implements MultipartFile {
        private final Part part;
        private final String filename;

        public PartMultipartFileAdapter(Part part) {
            this.part = part;
            String fn = part.getSubmittedFileName();
            this.filename = (fn != null && !fn.isBlank()) ? fn : "evidencia.jpg";
        }

        @Override public String getName() { return part.getName(); }
        @Override public String getOriginalFilename() { return filename; }
        @Override public String getContentType() { return part.getContentType(); }
        @Override public boolean isEmpty() { return part.getSize() == 0; }
        @Override public long getSize() { return part.getSize(); }
        @Override public byte[] getBytes() throws IOException { return part.getInputStream().readAllBytes(); }
        @Override public InputStream getInputStream() throws IOException { return part.getInputStream(); }
        @Override public void transferTo(File dest) throws IOException {
            try (InputStream in = part.getInputStream()) {
                Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * Adaptador en memoria para streams de imagen binaria directa.
     */
    private static class InMemoryMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public InMemoryMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename != null ? originalFilename : "evidencia.jpg";
            this.contentType = contentType != null ? contentType : "image/jpeg";
            this.content = content != null ? content : new byte[0];
        }

        @Override public String getName() { return name; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() { return content; }
        @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }
        @Override public void transferTo(File dest) throws IOException { Files.write(dest.toPath(), content); }
        @Override public void transferTo(Path dest) throws IOException { Files.write(dest, content); }
    }
}
