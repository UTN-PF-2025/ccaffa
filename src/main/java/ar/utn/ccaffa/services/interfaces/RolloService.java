package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.FiltroRolloDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.entity.Rollo;

import java.util.List;

public interface RolloService {
    List<RolloDto> findAll();
    List<RolloDto> filtrarRollos(FiltroRolloDto filtros);
    RolloDto findById(Long id);
    RolloDto findByIdConRollosPadres(Long id);
    RolloDto obtenerArbolCompletoDeHijos(Long rolloId);
    List<RolloDto> obtenerRollosDisponiblesParaOrdenVenta(Long ordenVentaId);
    RolloDto save(RolloDto rollo);
    boolean deleteById(Long id);
} 