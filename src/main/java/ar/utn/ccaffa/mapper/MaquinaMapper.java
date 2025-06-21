package ar.utn.ccaffa.mapper;

import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.entity.Maquina;
import org.springframework.stereotype.Component;

@Component
public class MaquinaMapper {
    
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
        return Maquina.builder()
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .estado(dto.getEstado())
                .velocidadTrabajo(dto.getVelocidadTrabajo())
                .build();
    }
} 