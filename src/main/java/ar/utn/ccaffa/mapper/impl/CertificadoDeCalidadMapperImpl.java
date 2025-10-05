package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.CertificadoDeCalidadMapper;
import ar.utn.ccaffa.model.dto.CertificadoDeCalidadDto;
import ar.utn.ccaffa.model.dto.EmpleadoDto;
import ar.utn.ccaffa.model.entity.CertificadoDeCalidad;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.Usuario;
import ar.utn.ccaffa.repository.interfaces.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CertificadoDeCalidadMapperImpl implements CertificadoDeCalidadMapper {
    private UsuarioRepository usuarioRepository;
    @Override
    public CertificadoDeCalidad toEntity(CertificadoDeCalidadDto certificado) {
        if (certificado == null) {
            return null;
        }

        return CertificadoDeCalidad.builder()
                .id(certificado.getId())
                .numeroDeCertificado(certificado.getNumeroDeCertificado())
                .fechaDeEmision(certificado.getFechaDeEmision())
                .aprobadorId(certificado.getAprobador() != null
                        ? certificado.getAprobador().getId()
                        : null)
                .controlDeCalidad(ControlDeCalidad.builder().id(certificado.getControlDeCalidadId()).build())
                .nombreArchivo(certificado.getNombreArchivo())
                .build();
    }

    @Override
    public CertificadoDeCalidadDto toDto(Optional<CertificadoDeCalidad> byId) {
        if (byId.isEmpty()) {
            return null;
        }

        CertificadoDeCalidad certificado = byId.get();
        String nombre = "";
        if (certificado.getAprobadorId() != null){
            Optional<Usuario> usuario = this.usuarioRepository.findById(certificado.getAprobadorId());
            if (usuario.isPresent()) { nombre = usuario.get().getNombre();}
        }


        return CertificadoDeCalidadDto.builder()
                .id(certificado.getId())
                .numeroDeCertificado(certificado.getNumeroDeCertificado())
                .fechaDeEmision(certificado.getFechaDeEmision())
                .aprobador(certificado.getAprobadorId() != null
                        ? EmpleadoDto.builder()
                        .id(certificado.getAprobadorId())
                        .nombre(nombre)
                        .build()
                        : null)
                .controlDeCalidadId(certificado.getControlDeCalidad() != null
                        ? certificado.getControlDeCalidad().getId()
                        : null)
                .nombreArchivo(certificado.getNombreArchivo())
                .build();
    }


}
