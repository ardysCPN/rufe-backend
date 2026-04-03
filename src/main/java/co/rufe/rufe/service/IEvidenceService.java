package co.rufe.rufe.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IEvidenceService {
    String uploadEvidence(MultipartFile file, String subFolder) throws IOException;
    String getEvidenceUrl(String subFolder, String filename);
}
