package ar.utn.ccaffa.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AnalysisService {
    void analyzeAndNotifyMock(MultipartFile file);

    /**
     * Overload that accepts a camera identifier to propagate through the analysis flow.
     */
    void analyzeAndNotify(MultipartFile file, String id, String cameraId) throws IOException;

    /**
     * Overload that accepts a camera identifier to propagate through the analysis flow.
     */
    void processAnalysis(byte[] fileBytes, String originalFilename, String contentType, String id, String cameraId);
}
