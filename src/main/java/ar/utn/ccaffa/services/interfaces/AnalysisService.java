package ar.utn.ccaffa.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface AnalysisService {
    void analyzeAndNotify(MultipartFile file);
    void analyzeAndNotifyMock(MultipartFile file);
}
