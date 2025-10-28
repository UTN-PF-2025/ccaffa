package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.enums.EstadoOrdenTrabajoMaquinaEnum;
import ar.utn.ccaffa.model.dto.metrics.OTMMetricsTotalByEstado;
import ar.utn.ccaffa.model.dto.metrics.OVMetricsTotalByEstado;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.model.entity.OrdenDeTrabajoMaquina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenDeTrabajoMaquinaRepository extends JpaRepository<OrdenDeTrabajoMaquina, Long> {
    List<OrdenDeTrabajoMaquina> findOrdenDeTrabajoMaquinaByEstadoAndFechaFinAfterAndFechaFinBeforeAndMaquinaIn(String estado, LocalDateTime fechaFinDesde, LocalDateTime fecaFinHasta, List<Maquina> maquinas);
    List<OrdenDeTrabajoMaquina> findOrdenDeTrabajoMaquinaByEstadoInAndFechaFinAfterAndFechaFinBeforeAndMaquinaIn(List <EstadoOrdenTrabajoMaquinaEnum> estados, LocalDateTime fechaFinDesde, LocalDateTime fecaFinHasta, List<Maquina> maquinas);

    @EntityGraph(attributePaths = {"maquina"})
    OrdenDeTrabajoMaquina findTopByOrdenDeTrabajo_IdOrderByFechaInicioDesc(Long ordenDeTrabajoId);

    List<OrdenDeTrabajoMaquina> findByMaquinaId(Long maquinaId);

    @EntityGraph(attributePaths = {"maquina", "rolloAUsar"})
    Page<OrdenDeTrabajoMaquina> findByMaquinaIdAndEstadoInOrderByFechaInicioAsc(Long maquinaId, List<EstadoOrdenTrabajoMaquinaEnum> estados, Pageable pageable);

    OrdenDeTrabajoMaquina findTopByMaquina_IdAndEstadoOrderByFechaInicioAsc(Long maquinaId, EstadoOrdenTrabajoMaquinaEnum estado);

    Boolean existsByMaquinaIsAndEstadoIs(Maquina maquina, EstadoOrdenTrabajoMaquinaEnum estadoOrdenTrabajoMaquinaEnum);

    @Query("SELECT new ar.utn.ccaffa.model.dto.metrics.OTMMetricsTotalByEstado(e.estado, COUNT(e)) FROM OrdenDeTrabajoMaquina e WHERE e.fechaInicio >= :fechaInicioDesde  GROUP BY e.estado")
    List<OTMMetricsTotalByEstado> totalByEstado(@Param("fechaInicioDesde") LocalDateTime fechaInicioDesde);
}
