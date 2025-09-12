package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoMaquinaMapper;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdenDeTrabajoMaquinaMapperImpl implements OrdenDeTrabajoMaquinaMapper {

    @Override
    public OrdenDeTrabajoMaquinaDto toDto(OrdenDeTrabajoMaquina entity) {
        return OrdenDeTrabajoMaquinaDto.builder()
                .id(entity.getId())
                .ordenDeTrabajoId(entity.getOrdenDeTrabajo() != null ? entity.getOrdenDeTrabajo().getId() : null)
                .maquinaId(entity.getMaquina() != null ? entity.getMaquina().getId() : null)
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .estado(entity.getEstado().name())
                .observaciones(entity.getObservaciones())
                .build();
    }

    @Override
    public List<OrdenDeTrabajoMaquinaDto> toDtoList(List<OrdenDeTrabajoMaquina> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
