package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.MaquinaMapper;
import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.entity.Maquina;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaquinaMapperImpl implements MaquinaMapper {
    
    public MaquinaDto toDto(Maquina entity) {
        return MaquinaDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .tipo(entity.getTipo())
                .estado(entity.getEstado())
                .velocidadTrabajo(entity.getVelocidadTrabajo())
                .build();
    }
    
    public Maquina toEntity(MaquinaDto dto) {
        if (dto == null) {
            return null;
        }
        return Maquina.builder()
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .estado(dto.getEstado())
                .velocidadTrabajo(dto.getVelocidadTrabajo())
                .build();
    }

    @Override
    public List<MaquinaDto> toDtoList(List<Maquina> maquinas) {
        if (maquinas == null) {
            return List.of();
        }
        return maquinas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Maquina> toEntityList(List<MaquinaDto> maquinasDtos) {
        if (maquinasDtos == null) {
            return List.of();
        }
        return maquinasDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 