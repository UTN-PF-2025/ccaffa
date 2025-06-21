package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.EspecificacionDto;
import ar.utn.ccaffa.model.entity.Especificacion;

import java.util.List;

public interface EspecificacionMapper {

    EspecificacionDto toDto(Especificacion especificacion);

    Especificacion toEntity(EspecificacionDto especificacionDto);

    List<EspecificacionDto> toDtoList(List<Especificacion> especificaciones);

    List<Especificacion> toEntityList(List<EspecificacionDto> especificacionDtos);

} 