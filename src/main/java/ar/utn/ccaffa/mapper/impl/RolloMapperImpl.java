package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.entity.Rollo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolloMapperImpl implements RolloMapper {

    @Override
    public RolloDto toDto(Rollo rollo) {
        if (rollo == null) {
            return null;
        }

        return RolloDto.builder()
                .id(rollo.getId())
                .proveedorId(rollo.getProveedorId())
                .codigoProveedor(rollo.getCodigoProveedor())
                .pesoKG(rollo.getPesoKG())
                .anchoMM(rollo.getAnchoMM())
                .espesorMM(rollo.getEspesorMM())
                .tipoMaterial(rollo.getTipoMaterial())
                .estado(rollo.getEstado())
                .fechaIngreso(rollo.getFechaIngreso())
                .rolloPadre(this.toDto(rollo.getRolloPadre()))
                .rolloPadreId(rollo.getRolloPadreId())
                .ordeDeTrabajoAsociadaID(rollo.getOrdeDeTrabajoAsociadaID())
                .asociadaAOrdenDeTrabajo(rollo.getAsociadaAOrdenDeTrabajo())
                .build();
    }

    @Override
    public RolloDto toDtoOnlyWithRolloPadreID(Rollo rollo) {
        if (rollo == null) {
            return null;
        }

        return RolloDto.builder()
                .id(rollo.getId())
                .proveedorId(rollo.getProveedorId())
                .codigoProveedor(rollo.getCodigoProveedor())
                .pesoKG(rollo.getPesoKG())
                .anchoMM(rollo.getAnchoMM())
                .espesorMM(rollo.getEspesorMM())
                .tipoMaterial(rollo.getTipoMaterial())
                .estado(rollo.getEstado())
                .fechaIngreso(rollo.getFechaIngreso())
                .rolloPadreId(rollo.getRolloPadreId())
                .ordeDeTrabajoAsociadaID(rollo.getOrdeDeTrabajoAsociadaID())
                .asociadaAOrdenDeTrabajo(rollo.getAsociadaAOrdenDeTrabajo())
                .build();
    }

    @Override
    public RolloDto toDtoWithRolloHijos(Rollo rollo) {
        if (rollo == null) {
            return null;
        }

        RolloDto dto = RolloDto.builder()
                        .id(rollo.getId())
                        .proveedorId(rollo.getProveedorId())
                        .codigoProveedor(rollo.getCodigoProveedor())
                        .pesoKG(rollo.getPesoKG())
                        .anchoMM(rollo.getAnchoMM())
                        .espesorMM(rollo.getEspesorMM())
                        .tipoMaterial(rollo.getTipoMaterial())
                        .estado(rollo.getEstado())
                        .fechaIngreso(rollo.getFechaIngreso())
                        .rolloPadreId(rollo.getRolloPadreId())
                        .ordeDeTrabajoAsociadaID(rollo.getOrdeDeTrabajoAsociadaID())
                        .asociadaAOrdenDeTrabajo(rollo.getAsociadaAOrdenDeTrabajo())
                        .build();

        if (rollo.getHijos() != null && !rollo.getHijos().isEmpty()) {
            List<RolloDto> hijosDto = rollo.getHijos().stream()
                    .map(this::toDtoWithRolloHijos)
                    .collect(Collectors.toList());
            dto.setHijos(hijosDto);
        }

        return dto;
    }

    @Override
    public Rollo toEntity(RolloDto rolloDto) {
        if (rolloDto == null) {
            return null;
        }

        return Rollo.builder()
                .id(rolloDto.getId())
                .proveedorId(rolloDto.getProveedorId())
                .codigoProveedor(rolloDto.getCodigoProveedor())
                .pesoKG(rolloDto.getPesoKG())
                .anchoMM(rolloDto.getAnchoMM())
                .espesorMM(rolloDto.getEspesorMM())
                .tipoMaterial(rolloDto.getTipoMaterial())
                .estado(rolloDto.getEstado())
                .fechaIngreso(rolloDto.getFechaIngreso())
                .rolloPadre(this.toEntity(rolloDto.getRolloPadre()))
                .ordeDeTrabajoAsociadaID(rolloDto.getOrdeDeTrabajoAsociadaID())
                .asociadaAOrdenDeTrabajo(rolloDto.getAsociadaAOrdenDeTrabajo())
                .build();

    }

    @Override
    public List<RolloDto> toDtoList(List<Rollo> rollos) {
        if (rollos == null) {
            return List.of();
        }
        return rollos.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RolloDto> toDtoListOnlyWithRolloPadreID(List<Rollo> rollos) {
        if (rollos == null) {
            return List.of();
        }
        return rollos.stream()
                .map(this::toDtoOnlyWithRolloPadreID)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rollo> toEntityList(List<RolloDto> rolloDtos) {
        if (rolloDtos == null) {
            return List.of();
        }
        return rolloDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

}
