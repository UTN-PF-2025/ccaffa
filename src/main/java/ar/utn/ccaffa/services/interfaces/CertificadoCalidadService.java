package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.CertificadoDeCalidadDto;
import ar.utn.ccaffa.model.dto.CertificadoRequestDTO;

public interface CertificadoCalidadService {
    void generarCertificado(CertificadoRequestDTO certificadoRequestDTO);
    CertificadoDeCalidadDto findById(Long id);
}
