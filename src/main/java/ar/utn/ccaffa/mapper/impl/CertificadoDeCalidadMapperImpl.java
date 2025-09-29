package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.CertificadoDeCalidadMapper;
import ar.utn.ccaffa.model.dto.CertificadoDeCalidadDto;
import ar.utn.ccaffa.model.dto.EmpleadoDto;
import ar.utn.ccaffa.model.entity.CertificadoDeCalidad;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.Empleado;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CertificadoDeCalidadMapperImpl implements CertificadoDeCalidadMapper {
    @Override
    public CertificadoDeCalidad toEntity(CertificadoDeCalidadDto certificado) {
        if (certificado == null) {
            return null;
        }

        return CertificadoDeCalidad.builder()
                .id(certificado.getId())
                .numeroDeCertificado(certificado.getNumeroDeCertificado())
                .fechaDeEmision(certificado.getFechaDeEmision())
                .aprobador(certificado.getAprobador() != null
                        ? Empleado.builder().id(certificado.getAprobador().getId()).build()
                        : null)
                .controlDeCalidad(ControlDeCalidad.builder().id(certificado.getControlDeCalidadId()).build())
                .build();
    }

    @Override
    public CertificadoDeCalidadDto toDto(Optional<CertificadoDeCalidad> byId) {
        if (byId.isEmpty()) {
            return null;
        }

        CertificadoDeCalidad certificado = byId.get();

        return CertificadoDeCalidadDto.builder()
                .id(certificado.getId())
                .numeroDeCertificado(certificado.getNumeroDeCertificado())
                .fechaDeEmision(certificado.getFechaDeEmision())
                .aprobador(certificado.getAprobador() != null
                        ? EmpleadoDto.builder()
                        .id(certificado.getAprobador().getId())
                        .nombre(certificado.getAprobador().getNombre())
                        .build()
                        : null)
                .controlDeCalidadId(certificado.getControlDeCalidad() != null
                        ? certificado.getControlDeCalidad().getId()
                        : null)
                .build();
    }


}
