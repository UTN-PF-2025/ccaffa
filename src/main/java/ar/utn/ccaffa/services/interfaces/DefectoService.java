package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.DefectoDto;

public interface DefectoService {
    DefectoDto findById(Long id);
}
