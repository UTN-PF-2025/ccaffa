package ar.utn.ccaffa.mapper.impl;

import ar.utn.ccaffa.mapper.interfaces.OrdenDeTrabajoMaquinaResponseMapper;
import ar.utn.ccaffa.mapper.interfaces.RolloMapper;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoMaquinaResponseDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import ar.utn.ccaffa.repository.interfaces.MaquinaRepository;
import ar.utn.ccaffa.repository.interfaces.OrdenDeTrabajoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdenDeTrabajoMaquinaResponseMapperImpl implements OrdenDeTrabajoMaquinaResponseMapper {
    private final MaquinaRepository maquinaRepository;
    private final RolloMapper rolloMapper;

    public OrdenDeTrabajoMaquinaResponseMapperImpl(MaquinaRepository maquinaRepository, RolloMapper rolloMapper) {
        this.maquinaRepository = maquinaRepository;
        this.rolloMapper = rolloMapper;
    }


    @Override
    public OrdenDeTrabajoMaquina toEntity(OrdenDeTrabajoMaquinaResponseDto ordenDeTrabajoMaquina, OrdenDeTrabajo ordenTrabajo){
        return OrdenDeTrabajoMaquina.builder()
                .ordenDeTrabajo(ordenTrabajo)
                .maquina(this.maquinaRepository.getReferenceById(ordenDeTrabajoMaquina.getMaquina().getId()))
                .fechaInicio(ordenDeTrabajoMaquina.getFechaInicio())
                .fechaFin(ordenDeTrabajoMaquina.getFechaFin())
                .estado(ordenDeTrabajoMaquina.getEstado())
                .observaciones(ordenDeTrabajoMaquina.getObservaciones())
                .rolloAUsar(this.rolloMapper.toEntity(ordenDeTrabajoMaquina.getRolloAUsar()))
                .build();
    }

    @Override
    public List<OrdenDeTrabajoMaquina> toEntityList(List<OrdenDeTrabajoMaquinaResponseDto> ordenesDeTrabajoMaquina, OrdenDeTrabajo ordenTrabajo){
        if (ordenesDeTrabajoMaquina == null) {
            return List.of();
        }
        return ordenesDeTrabajoMaquina.stream()
                .map( om -> this.toEntity(om,  ordenTrabajo))
                .collect(Collectors.toList());
    }
}
