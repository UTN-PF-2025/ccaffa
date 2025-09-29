package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.CertificadoDeCalidadDto;
import ar.utn.ccaffa.model.entity.CertificadoDeCalidad;

import java.util.Optional;

public interface CertificadoDeCalidadMapper {
    CertificadoDeCalidad toEntity(CertificadoDeCalidadDto certificado);

    CertificadoDeCalidadDto toDto(Optional<CertificadoDeCalidad> byId);
}
