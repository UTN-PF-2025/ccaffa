package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.entity.Rollo;

import java.util.List;

public interface RolloService {
    List<RolloDto> findAll();
    RolloDto findById(Long id);
    Rollo save(RolloDto rollo);
    boolean deleteById(Long id);
} 