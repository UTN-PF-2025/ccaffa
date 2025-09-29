package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaResponseDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;

import java.util.List;

public interface OrdenDeTrabajoMaquinaResponseMapper {

    OrdenDeTrabajoMaquina toEntity(OrdenDeTrabajoMaquinaResponseDto ordenDeTrabajoMaquina, OrdenDeTrabajo ordenTrabajo);
    List<OrdenDeTrabajoMaquina> toEntityList(List<OrdenDeTrabajoMaquinaResponseDto> ordenesDeTrabajoMaquina, OrdenDeTrabajo ordenTrabajo);
}
