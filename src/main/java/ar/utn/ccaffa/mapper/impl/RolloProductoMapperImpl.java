package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.*;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.dto.RolloProductoDto;
import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.model.entity.RolloProducto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolloProductoMapperImpl implements RolloProductoMapper {



    @Override
    public RolloProductoDto toDto(RolloProducto rollo) {
        if (rollo == null) {
            return null;
        }

        return RolloProductoDto.builder()
                .id(rollo.getId())
                .pesoKG(rollo.getPesoKG())
                .anchoMM(rollo.getAnchoMM())
                .espesorMM(rollo.getEspesorMM())
                .tipoMaterial(rollo.getTipoMaterial())
                .estado(rollo.getEstado())
                .fechaIngreso(rollo.getFechaIngreso())
                .rolloPadreId(rollo.getRolloPadreId())
                .ordenDeTrabajoId(rollo.getOrdenDeTrabajoId())
                .build();
    }

    @Override
    public RolloProducto toEntity(RolloProductoDto rolloProductoDto) {
        if (rolloProductoDto == null) {
            return null;
        }

        return RolloProducto.builder()
                .id(rolloProductoDto.getId())
                .pesoKG(rolloProductoDto.getPesoKG())
                .anchoMM(rolloProductoDto.getAnchoMM())
                .espesorMM(rolloProductoDto.getEspesorMM())
                .tipoMaterial(rolloProductoDto.getTipoMaterial())
                .estado(rolloProductoDto.getEstado())
                .fechaIngreso(rolloProductoDto.getFechaIngreso())
                .build();
    }

    @Override
    public List<RolloProductoDto> toDtoList(List<RolloProducto> rollos) {
        if (rollos == null) {
            return List.of();
        }
        return rollos.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RolloProducto> toEntityList(List<RolloProductoDto> rolloDtos) {
        if (rolloDtos == null) {
            return List.of();
        }
        return rolloDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

}