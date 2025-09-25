package ar.utn.ccaffa.services.interfaces;

import ar.utn.ccaffa.model.dto.FiltroOrdenDeTrabajoDto;
import ar.utn.ccaffa.model.dto.OrdenDeTrabajoResponseDto;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajo;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenDeTrabajoService {
    OrdenDeTrabajo save(OrdenDeTrabajo orden);

    List<OrdenDeTrabajo> saveAllDtos(List<OrdenDeTrabajoResponseDto> ordenes);
    List<OrdenDeTrabajo> findAll();
    Optional<OrdenDeTrabajo> findById(Long id);
    Optional<OrdenDeTrabajo> update(Long id, OrdenDeTrabajo orden);
    Optional<OrdenDeTrabajo> desactivar(Long id);

    OrdenDeTrabajo cancelarOrdenDeTrabajo(Long id);

    ar.utn.ccaffa.model.dto.CancelacionSimulacionDto simularCancelacion(Long id);
    List<OrdenDeTrabajo> findByRolloId(Long rolloId);
    OrdenDeTrabajo findByProcesoId(Long rolloId);
    List<OrdenDeTrabajo> filtrarOrdenes(FiltroOrdenDeTrabajoDto filtros);

    List<OrdenDeTrabajoMaquina> findOrdenDeTrabajoMaquinaByEstadoAndFechaFinAfterAndFechaFinBeforeAndMaquinaIn(String estado, LocalDateTime fechaFinDesde, LocalDateTime fecaFinHasta, List<Maquina> maquinas);
}