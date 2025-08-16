package ar.utn.ccaffa.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AnalysisService {
    void analyzeAndNotify(MultipartFile file) throws IOException;
    void processAnalysis(byte[] fileBytes, String originalFilename, String contentType);
    void analyzeAndNotifyMock(MultipartFile file);
}
