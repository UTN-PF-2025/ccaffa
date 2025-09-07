package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;

import java.util.List;

public interface OrdenDeTrabajoMaquinaMapper {
    OrdenDeTrabajoMaquinaDto toDto(OrdenDeTrabajoMaquina entity);
    List<OrdenDeTrabajoMaquinaDto> toDtoList(List<OrdenDeTrabajoMaquina> entities);
}
