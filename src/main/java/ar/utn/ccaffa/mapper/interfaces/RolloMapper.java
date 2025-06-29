package ar.utn.ccaffa.mapper.interfaces;


import ar.utn.ccaffa.model.entity.Rollo;
import ar.utn.ccaffa.model.dto.RolloDto;

import java.util.List;

public interface RolloMapper {
    RolloDto toDto(Rollo rollo);

    RolloDto toDtoOnlyWithRolloPadreID(Rollo rollo);

    Rollo toEntity(RolloDto rolloDto);

    List<RolloDto> toDtoList(List<Rollo> rollos);

    List<RolloDto> toDtoListOnlyWithRolloPadreID(List<Rollo> rollos);

    RolloDto toDtoWithRolloHijos(Rollo rollo);

    List<Rollo> toEntityList(List<RolloDto> rolloDtos);

}
