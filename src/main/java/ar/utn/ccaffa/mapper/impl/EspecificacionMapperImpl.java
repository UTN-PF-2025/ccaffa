package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.EspecificacionMapper;
import ar.utn.ccaffa.model.dto.EspecificacionDto;
import ar.utn.ccaffa.model.entity.Especificacion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EspecificacionMapperImpl implements EspecificacionMapper {

    @Override
    public EspecificacionDto toDto(Especificacion especificacion) {
        if (especificacion == null) {
            return null;
        }

        return EspecificacionDto.builder()
                .id(especificacion.getId())
                .ancho(especificacion.getAncho())
                .espesor(especificacion.getEspesor())
                .cantidad(especificacion.getCantidad())
                .tipoMaterial(especificacion.getTipoMaterial())
                .pesoMaximoPorRollo(especificacion.getPesoMaximoPorRollo())
                .tipoDeEmbalaje(especificacion.getTipoDeEmbalaje())
                .toleranciaAncho(especificacion.getToleranciaAncho())
                .toleranciaEspesor(especificacion.getToleranciaEspesor())
                .diametroInterno(especificacion.getDiametroInterno())
                .diametroExterno(especificacion.getDiametroExterno())
                .ordenVentaId(null)
                .build();
    }

    @Override
    public Especificacion toEntity(EspecificacionDto especificacionDto) {
        if (especificacionDto == null) {
            return null;
        }

        return Especificacion.builder()
                .id(especificacionDto.getId())
                .ancho(especificacionDto.getAncho())
                .espesor(especificacionDto.getEspesor())
                .cantidad(especificacionDto.getCantidad())
                .tipoMaterial(especificacionDto.getTipoMaterial())
                .pesoMaximoPorRollo(especificacionDto.getPesoMaximoPorRollo())
                .tipoDeEmbalaje(especificacionDto.getTipoDeEmbalaje())
                .toleranciaAncho(especificacionDto.getToleranciaAncho())
                .toleranciaEspesor(especificacionDto.getToleranciaEspesor())
                .diametroInterno(especificacionDto.getDiametroInterno())
                .diametroExterno(especificacionDto.getDiametroExterno())
                .build();
    }

    @Override
    public List<EspecificacionDto> toDtoList(List<Especificacion> especificaciones) {
        if (especificaciones == null) {
            return List.of();
        }
        return especificaciones.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Especificacion> toEntityList(List<EspecificacionDto> especificacionDtos) {
        if (especificacionDtos == null) {
            return List.of();
        }
        return especificacionDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 