package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoMaquinaMapper;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.model.entity.Rollo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdenDeTrabajoMaquinaMapperImpl implements OrdenDeTrabajoMaquinaMapper {

    private final RolloMapper rolloMapper;

    public OrdenDeTrabajoMaquinaMapperImpl(RolloMapper rolloMapper) {
        this.rolloMapper = rolloMapper;
    }

    @Override
    public OrdenDeTrabajoMaquinaDto toDto(OrdenDeTrabajoMaquina entity) {
        return OrdenDeTrabajoMaquinaDto.builder()
                .id(entity.getId())
                .ordenDeTrabajoId(entity.getOrdenDeTrabajo().getId())
                .maquinaId(entity.getMaquina().getId())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .estado(entity.getEstado())
                .observaciones(entity.getObservaciones())
                .rolloAUsar(this.rolloMapper.toDtoOnlyWithRolloPadreID(entity.getRolloAUsar()))
                .build();
    }

    @Override
    public List<OrdenDeTrabajoMaquinaDto> toDtoList(List<OrdenDeTrabajoMaquina> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
