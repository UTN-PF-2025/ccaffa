package ar.utn.ccaffa.mapper;

import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.entity.Maquina;
import org.springframework.stereotype.Component;

@Component
public class MaquinaMapper {
    
    public MaquinaDto toDto(Maquina entity) {
        return MaquinaDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .estado(entity.getEstado())
                .velocidadTrabajo(entity.getVelocidadTrabajo())
                .build();
    }
    
    public Maquina toEntity(MaquinaDto dto) {
        return Maquina.builder()
                .id(dto.getId() != 0 ? dto.getId() : null)
                .name(dto.getName())
                .type(dto.getType())
                .estado(dto.getEstado())
                .velocidadTrabajo(dto.getVelocidadTrabajo())
                .build();
    }
} 