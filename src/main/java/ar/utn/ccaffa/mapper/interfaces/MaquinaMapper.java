package ar.utn.ccaffa.mapper.interfaces;


import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.entity.Maquina;

import java.util.List;

public interface MaquinaMapper {
    MaquinaDto toDto(Maquina maquina);

    Maquina toEntity(MaquinaDto maquinaDto);

    List<MaquinaDto> toDtoList(List<Maquina> maquinas);

    List<Maquina> toEntityList(List<MaquinaDto> maquinasDtos);

}
