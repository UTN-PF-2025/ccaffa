package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.FiltroRolloProductoDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.dto.RolloProductoDto;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;

import java.util.List;

public interface RolloProductoService {
    List<RolloProductoDto> findAll();
    List<RolloProductoDto> filtrarRollosProducto(FiltroRolloProductoDto filtros);

    RolloProductoDto findById(Long id);

    RolloProductoDto save(RolloProductoDto rollo);

    boolean deleteById(Long id);

    List<RolloProductoDto> findByRolloPadreId(Long rolloId);
    List<RolloProductoDto> findByOrdenDeTrabajoId(Long ordenId);

} 