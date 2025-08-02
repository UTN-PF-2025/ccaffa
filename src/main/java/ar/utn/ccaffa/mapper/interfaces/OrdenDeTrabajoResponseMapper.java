package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.OrdenDeTrabajoResponseDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;

import java.util.List;

public interface OrdenDeTrabajoResponseMapper {
    OrdenDeTrabajoResponseDto toDto(OrdenDeTrabajo ordenDeTrabajo);
    
    List<OrdenDeTrabajoResponseDto> toDtoList(List<OrdenDeTrabajo> ordenesDeTrabajo);
} 