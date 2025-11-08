package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.CertificadoDeCalidadDto;
import ar.utn.ccaffa.model.dto.CertificadoRequestDTO;

public interface CertificadoCalidadService {
    byte[] generarCertificado(CertificadoRequestDTO certificadoRequestDTO);
    CertificadoDeCalidadDto findById(Long id);

    byte[] obtenerPdf(Long certificadoId);
}
