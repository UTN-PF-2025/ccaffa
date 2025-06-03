package ar.utn.ccaffa.services.interfaces;

import java.util.List;

import ar.utn.ccaffa.model.dto.MaquinaDto;

public interface MaquinaService {
    List<MaquinaDto> obtenerTodos();

    MaquinaDto obtenerPorId(Long id);

    MaquinaDto guardar(MaquinaDto entidad);

    void eliminar(Long id);
}
