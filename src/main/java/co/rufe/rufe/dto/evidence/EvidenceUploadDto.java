package co.rufe.rufe.dto.evidence;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceUploadDto {

    private String fileName;

    private String contentType;

    @NotBlank(message = "La imagen codificada en Base64 es obligatoria")
    private String imageBase64;

    private String subFolder;
}
