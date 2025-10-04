package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.model.dto.FiltroRolloDto;
import ar.utn.ccaffa.model.dto.RolloDto;
import ar.utn.ccaffa.model.dto.ModificarRolloRequestDto;
import ar.utn.ccaffa.model.entity.Rollo;

import java.util.List;

public interface RolloService {
    List<RolloDto> findAll();
    List<RolloDto> filtrarRollos(FiltroRolloDto filtros);
    RolloDto findById(Long id);
    RolloDto findByIdConRollosPadres(Long id);
    RolloDto obtenerArbolCompletoDeRollosHijos(Long rolloId);
    List<RolloDto> obtenerRollosDisponiblesParaOrdenVenta(Long ordenVentaId);

    List<Rollo> findEntitiesByEstadosAndAsociado(List<EstadoRollo> estadosRollo, Boolean asociado);

    List<Rollo> findEntitiesByIdIn(List<Long> ids);

    boolean existsRolloByProveedorIdAndCodigoProveedor(Long proovedorId, String codigoProveedor);
    RolloDto save(RolloDto rollo);
    boolean deleteById(Long id);
    RolloDto modificarRollo(ModificarRolloRequestDto request);
    boolean anularRollo(Long id);

    boolean estaDisponible(Rollo rollo);
    List<Long> simularAnularRollo(Long id);
}