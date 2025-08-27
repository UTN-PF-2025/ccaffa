package ar.utn.ccaffa.mapper.interfaces;

import ar.utn.ccaffa.model.dto.CamaraDto;
import ar.utn.ccaffa.model.entity.Camara;

import java.util.List;

public interface CamaraMapper {
    CamaraDto toDto(Camara camara);

    Camara toEntity(CamaraDto camaraDto);

    List<CamaraDto> toDtoList(List<Camara> camaras);

    List<Camara> toEntityList(List<CamaraDto> camaraDtos);
} 