package ar.utn.ccaffa.services.interfaces;

import java.util.List;

import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.entity.Maquina;

public interface MaquinaService {
    List<MaquinaDto> findAll();

    MaquinaDto findById(Long id);

    Maquina save(MaquinaDto entidad);

    boolean deleteById(Long id);

    List<Maquina> findAllIDsOfAvailableMachinesEntity();

    List<MaquinaDto> findAllIDsOfAvailableMachinesDto();

    List<Maquina> findByIdIn(List<Long> ids);
}
