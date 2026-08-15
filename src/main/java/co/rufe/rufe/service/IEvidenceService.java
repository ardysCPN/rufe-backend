package co.rufe.rufe.service;

import co.rufe.rufe.dto.evidence.EvidenceUploadDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IEvidenceService {
    String uploadEvidence(MultipartFile file, String subFolder) throws IOException;
    String uploadEvidenceBase64(EvidenceUploadDto dto) throws IOException;
    String getEvidenceUrl(String subFolder, String filename);
}
