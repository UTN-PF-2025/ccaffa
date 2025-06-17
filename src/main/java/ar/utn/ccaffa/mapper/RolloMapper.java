package ar.utn.ccaffa.mapper;


import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.model.dto.RolloDto;

import java.util.List;

public interface RolloMapper {
    RolloDto toDto(Rollo rollo);

    Rollo toEntity(RolloDto rolloDto);

    List<RolloDto> toDtoList(List<Rollo> rollos);

    List<Rollo> toEntityList(List<RolloDto> rolloDtos);

}
