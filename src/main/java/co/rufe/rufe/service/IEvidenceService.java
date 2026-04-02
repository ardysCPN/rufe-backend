package co.rufe.rufe.service;

public interface IEvidenceService {
    String generatePresignedUploadUrl(String bucketName, String objectKey, String contentType);
    String generatePresignedDownloadUrl(String bucketName, String objectKey);
}
